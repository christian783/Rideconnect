package app.io.rideconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideTrackingDTO {
    private String id;
    private String rideId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Double speed;
    private Double heading;
}
