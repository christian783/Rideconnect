package app.io.rideconnect.dto;

import app.io.rideconnect.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private String status;
    private String profileImageUrl;
    private String preferredLanguage;
    private String preferredCurrency;
    private LocationDTO currentLocation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private List<UserLocationDTO> locations;
}