package app.io.rideconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotspotDTO {
    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double radiusMeters;
    private BigDecimal demandScore;
    private Integer averageRidesPerHour;
    private String timeOfDayPattern;
    private LocalDateTime updatedAt;
}
