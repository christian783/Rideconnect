package app.io.rideconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotspotResponseDTO {
    private Long id;
    private String name;
    private LocationDTO location;
    private Double radiusMeters;
    private BigDecimal demandScore;
    private Integer averageRidesPerHour;
    private String timeOfDayPattern;
}