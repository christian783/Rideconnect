package app.io.rideconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sms")
@Data
public class SmsConfig {
    private String apiUrl;
    private String apiKey;
    private String senderId;
}
