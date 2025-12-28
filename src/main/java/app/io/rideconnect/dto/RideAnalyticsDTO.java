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
public class RideAnalyticsDTO {
    private Long totalRides;
    private Long completedRides;
    private Long cancelledRides;
    private BigDecimal totalRevenue;
    private BigDecimal averageFare;
    private Double averageRating;
}