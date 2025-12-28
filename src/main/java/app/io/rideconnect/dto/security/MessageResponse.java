package app.io.rideconnect.dto.security;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private boolean success;
    private String message;
}