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
public class DeliveryProofDTO {
    @NotNull(message = "Delivery ID is required")
    private Long deliveryId;

    private String proofOfDeliveryUrl;
    private String recipientSignatureUrl;
}
