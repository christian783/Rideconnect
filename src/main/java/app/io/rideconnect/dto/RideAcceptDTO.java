package app.io.rideconnect.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideAcceptDTO {
    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Ride ID is required")
    private String rideId;
}