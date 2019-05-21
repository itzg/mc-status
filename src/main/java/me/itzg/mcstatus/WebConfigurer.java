package me.itzg.mcstatus;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Geoff Bourne
 * @since Jun 2018
 */
@Configuration @Slf4j
public class WebConfigurer implements WebMvcConfigurer {
    public static final String CORS_PATH = "/**";
    private final CorsProperties corsProperties;

    @Autowired
    public WebConfigurer(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final String[] allowedOrigins = corsProperties.getAllowOrigins();
        if (allowedOrigins != null && allowedOrigins.length > 0) {
            log.info("Allowing origins: {}", Arrays.asList(allowedOrigins));
            registry.addMapping(CORS_PATH).allowedOrigins(allowedOrigins);
        }
        else if (corsProperties.isAllowAll()) {
            log.info("Allowing all origins");
            registry.addMapping(CORS_PATH).allowedOrigins("*");
        }
    }
}
