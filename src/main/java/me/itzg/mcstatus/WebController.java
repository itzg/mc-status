package me.itzg.mcstatus;

import com.google.common.net.HostAndPort;
import lombok.extern.slf4j.Slf4j;
import me.itzg.mcstatus.model.ServerStatus;
import me.itzg.mcstatus.services.ServerStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@RestController
@Slf4j
public class WebController {

    private final ServerStatusService serverStatusService;

    @Autowired
    public WebController(ServerStatusService serverStatusService) {
        this.serverStatusService = serverStatusService;
    }

    @GetMapping("/servers")
    public List<ServerStatus> getAllServersStatus() {

        return serverStatusService.getAllServersStatus();
    }

    @GetMapping("/server")
    public ServerStatus getServerStatus(@RequestParam String host, @RequestParam(defaultValue = "25565") int port) throws ServerTimeoutException {

        return serverStatusService.getServerStatus(host, port);
    }

    private ServerStatus getServerStatus(HostAndPort hostAndPort) throws ServerTimeoutException {

        return serverStatusService.getServerStatus(hostAndPort);
    }
}
