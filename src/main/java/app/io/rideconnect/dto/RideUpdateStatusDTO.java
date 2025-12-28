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
public class RideUpdateStatusDTO {
    @NotNull(message = "Ride ID is required")
    private String rideId;

    @NotNull(message = "Status is required")
    private String status;

    private LocationDTO currentLocation;
}
