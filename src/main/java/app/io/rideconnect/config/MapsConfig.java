package app.io.rideconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "maps")
@Data
public class MapsConfig {
    private String apiKey;
    private String baseUrl;
}