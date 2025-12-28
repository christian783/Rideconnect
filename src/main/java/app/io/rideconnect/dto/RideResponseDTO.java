package app.io.rideconnect.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
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
public class RideResponseDTO {
    private Long id;
    private String passengerId;
    private Long driverId;
    private String rideType;
    private String status;
    private LocationDTO pickupLocation;
    private LocationDTO dropoffLocation;
    private String pickupAddress;
    private String dropoffAddress;
    private BigDecimal estimatedFare;
    private BigDecimal actualFare;
    private BigDecimal distanceKm;
    private Integer estimatedDurationMinutes;
    private Integer actualDurationMinutes;
    private String paymentMethod;
    private String paymentStatus;
    private Boolean isCarpoolEnabled;
    private Boolean sosActivated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime acceptedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    private UserResponseDTO passenger;
    private DriverProfileResponseDTO driver;
    private DeliveryDetailsDTO deliveryDetails;
}