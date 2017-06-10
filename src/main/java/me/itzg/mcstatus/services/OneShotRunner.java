package me.itzg.mcstatus.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import lombok.extern.slf4j.Slf4j;
import me.itzg.mcstatus.ServerTimeoutException;
import me.itzg.mcstatus.model.ServerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@Service
@Slf4j
public class OneShotRunner implements ApplicationRunner {

    private final ServerStatusService serverStatusService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OneShotRunner(ServerStatusService serverStatusService, ObjectMapper objectMapper) {
        this.serverStatusService = serverStatusService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        final List<String> args = applicationArguments.getNonOptionArgs();

        if (!args.isEmpty()) {
            final Optional<String> results = applicationArguments.getOptionValues("results").stream().findFirst();

            final List<PrintStream> outputs = new ArrayList<>();
            outputs.add(System.out);

            if (results.isPresent()) {
                outputs.add(new PrintStream(new FileOutputStream(results.get())));
            }

            final Integer totalFails;
            try {
                totalFails = args.stream().reduce(0, (fails, s) -> {
                    try {
                        final ServerStatus serverStatus = serverStatusService.getServerStatus(HostAndPort.fromString(s));

                        try {
                            final String str = objectMapper.writeValueAsString(serverStatus);
                            outputs.forEach(out -> out.println(str));
                        } catch (JsonProcessingException e) {
                            log.error("Failed to serialize JSON for {}", serverStatus, e);
                            outputs.forEach(out -> out.println("ERROR"));
                            return 1;
                        }

                        return 0;
                    } catch (ServerTimeoutException e) {
                        log.error("Timed out requesting status from {}", s);
                        outputs.forEach(out -> out.println("ERROR"));
                        return 1;
                    }
                }, (f1, f2) -> f1 + f2);
            } finally {
                if (results.isPresent()) {
                    log.info("Wrote results to {}", results.get());
                    outputs.get(1).close();
                }
            }

            System.exit(totalFails);
        }
    }
}
