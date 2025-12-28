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
public class RideRequestDTO {
    @NotNull(message = "Passenger ID is required")
    private String passengerId;

    @NotNull(message = "Ride type is required")
    private String rideType;

    @NotNull(message = "Pickup location is required")
    private LocationDTO pickupLocation;

    @NotNull(message = "Dropoff location is required")
    private LocationDTO dropoffLocation;

    private String pickupAddress;
    private String dropoffAddress;

    private String paymentMethod;
    private Boolean isCarpoolEnabled;
    private Integer carpoolSeatsAvailable;

    // For delivery rides
    private DeliveryDetailsDTO deliveryDetails;
}