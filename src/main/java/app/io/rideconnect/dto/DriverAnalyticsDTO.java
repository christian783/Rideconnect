package app.io.rideconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverAnalyticsDTO {
    private Long totalDrivers;
    private Long activeDrivers;
    private Long verifiedDrivers;
    private Integer bronzeDrivers;
    private Integer silverDrivers;
    private Integer goldDrivers;
    private Integer platinumDrivers;
}
