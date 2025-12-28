package app.io.rideconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfileRequestDTO {
    @NotNull(message = "User ID is required")
    private String userId;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String licenseImageUrl;

    @NotBlank(message = "Vehicle registration is required")
    private String vehicleRegistration;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    private String vehicleModel;
    private String vehicleColor;
    private Integer vehicleYear;
    private String referralCode;
}

