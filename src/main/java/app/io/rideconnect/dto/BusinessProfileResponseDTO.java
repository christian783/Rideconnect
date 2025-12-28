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
public class BusinessProfileResponseDTO {
    private Long id;
    private String userId;
    private String businessName;
    private String taxId;
    private String businessAddress;
    private BigDecimal walletBalance;
}