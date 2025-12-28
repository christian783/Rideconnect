package app.io.rideconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDetailsDTO {
    private String packageDescription;
    private String packageWeight;
    private String packageDimensions;
    private String recipientName;
    private String recipientPhone;
    private String deliveryNotes;
}
