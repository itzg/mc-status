package me.itzg.mcstatus;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Jun 2018
 */
@ConfigurationProperties("cors") @Component
@Data
public class CorsProperties {
    boolean allowAll = true;

    String[] allowOrigins;
}
