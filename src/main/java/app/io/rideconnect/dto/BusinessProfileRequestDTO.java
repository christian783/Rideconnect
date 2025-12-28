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
public class BusinessProfileRequestDTO {
    @NotNull(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String taxId;
    private String businessAddress;
}
