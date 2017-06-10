package me.itzg.mcstatus;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@ConfigurationProperties("mcstatus")
@Component @Data
public class AppProperties {
    int defaultPort = 25565;
    List<String> servers = Collections.emptyList();
    int serverInfoTimeoutSec = 10;

    boolean excludeIcon = false;
    boolean excludePlayers = false;
}
