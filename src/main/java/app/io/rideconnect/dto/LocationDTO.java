package app.io.rideconnect.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private Boolean isPrimary;
}