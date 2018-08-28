package me.itzg.mcstatus.services;

import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.common.net.HostAndPort;
import lombok.extern.slf4j.Slf4j;
import me.itzg.mcstatus.AppProperties;
import me.itzg.mcstatus.ServerTimeoutException;
import me.itzg.mcstatus.model.Motd;
import me.itzg.mcstatus.model.Player;
import me.itzg.mcstatus.model.ServerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ServerStatusService {
    public final AppProperties properties;
    private final FormattedMessageConverter formattedMessageConverter;

    @Autowired
    public ServerStatusService(AppProperties properties, FormattedMessageConverter formattedMessageConverter) {
        this.properties = properties;
        this.formattedMessageConverter = formattedMessageConverter;
    }

    @GetMapping("/servers")
    public List<ServerStatus> getAllServersStatus() {

        return properties.getServers().stream()
                .map(HostAndPort::fromString)
                .map(hostAndPort -> {
                    try {
                        return getServerStatus(hostAndPort);
                    } catch (ServerTimeoutException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ServerStatus getServerStatus(String host, int port) throws ServerTimeoutException {

        return getServerStatus(HostAndPort.fromParts(host, port));
    }

    public ServerStatus getServerStatus(HostAndPort hostAndPort) throws ServerTimeoutException {
        final int port = hostAndPort.getPortOrDefault(properties.getDefaultPort());

        final CompletableFuture<ServerStatus> infoFuture = new CompletableFuture<ServerStatus>();

        final MinecraftProtocol mcProto = new MinecraftProtocol(SubProtocol.STATUS);
        Client client = new Client(hostAndPort.getHostText(), port, mcProto, new TcpSessionFactory());
        client.getSession().setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, new ServerInfoHandler() {
            @Override
            public void handle(Session session, ServerStatusInfo info) {
                final ServerStatus status = new ServerStatus();
                status.setHost(hostAndPort.getHostText());
                status.setPort(port);

                status.setVersion(info.getVersionInfo().getVersionName());
                status.setProtocolVersion(info.getVersionInfo().getProtocolVersion());

                String rawMotd = info.getDescription().getText();
                status.setDescription(rawMotd); // for backward compatibility
                if (rawMotd.trim().isEmpty() && !info.getDescription().getExtra().isEmpty()) {
                    rawMotd = formattedMessageConverter.convertPartsToCode(info.getDescription().getExtra());
                }
                status.setMotd(new Motd());
                status.getMotd().setRaw(rawMotd);
                status.getMotd().setHtml(formattedMessageConverter.convertToHtml(rawMotd));
                status.getMotd().setStripped(formattedMessageConverter.stripCodes(rawMotd));

                status.getPlayers().setOnline(info.getPlayerInfo().getOnlinePlayers());
                status.getPlayers().setMax(info.getPlayerInfo().getMaxPlayers());
                if (!properties.isExcludePlayers()) {
                    if (info.getPlayerInfo().getPlayers() != null) {
                        status.getPlayers().setPlayers(Stream.of(info.getPlayerInfo().getPlayers())
                                                               .map(gameProfile -> {
                                                                   final Player player = new Player();
                                                                   player.setName(gameProfile.getName());
                                                                   player.setId(gameProfile.getIdAsString());
                                                                   return player;
                                                               })
                                                               .collect(Collectors.toList()));
                    }
                }

                if (!properties.isExcludeIcon()) {
                    final ByteArrayOutputStream iconBytesOut = new ByteArrayOutputStream();
                    if (info.getIcon() != null) {
                        try {
                            ImageIO.write(info.getIcon(), "png", iconBytesOut);
                            status.setIcon(iconBytesOut.toByteArray());
                        } catch (IOException e) {
                            log.warn("Failed to write image bytes of server icon {} from {}",
                                                   e,
                                                   info.getIcon(),
                                                   hostAndPort);
                        }
                    } else {
                        log.debug("No server icon for {}", status);
                    }
                }

                infoFuture.complete(status);
            }
        });
        log.info("Getting info from {}", hostAndPort);
        client.getSession().connect();

        try {
            try {
                return infoFuture.get(properties.getServerInfoTimeoutSec(), TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.warn("Timed out getting server info from {}", hostAndPort);
                if (client.getSession().isConnected()) {
                    log.debug("Disconnecting from {}", hostAndPort);
                    client.getSession().disconnect("timeout");
                }
                throw new ServerTimeoutException(String.format("Timed out getting server info from %s after %d seconds",
                                                               hostAndPort, properties.getServerInfoTimeoutSec()));
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Unable to request status from {}", hostAndPort, e);
            return null;
        }
    }
}