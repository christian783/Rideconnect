package app.io.rideconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {

    private BaseFare baseFare = new BaseFare();
    private PerKmRate perKmRate = new PerKmRate();
    private BigDecimal commissionRate;
    private BigDecimal surgeMultiplier;
    private Double maxDriverSearchRadius;
    private Integer rideRequestTimeout;

    @Data
    public static class BaseFare {
        private BigDecimal motorcycle;
        private BigDecimal car;
        private BigDecimal van;
    }

    @Data
    public static class PerKmRate {
        private BigDecimal motorcycle;
        private BigDecimal car;
        private BigDecimal van;
    }
}
