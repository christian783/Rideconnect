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
public class DriverProfileResponseDTO {
    private String id;
    private String userId;
    private String licenseNumber;
    private String licenseImageUrl;
    private String vehicleRegistration;
    private String vehicleType;
    private String vehicleModel;
    private String vehicleColor;
    private Integer vehicleYear;
    private Boolean isVerified;
    private String tier;
    private BigDecimal rating;
    private Integer totalRides;
    private Boolean isAvailable;
    private LocationDTO lastKnownLocation;
    private String referralCode;
}
