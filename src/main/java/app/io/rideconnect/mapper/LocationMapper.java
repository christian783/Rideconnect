package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.LocationDTO;
import app.io.rideconnect.dto.UserLocationDTO;
import app.io.rideconnect.entity.UserLocation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDTO toDTO(UserLocation location);

    UserLocation toEntity(LocationDTO dto);

    List<LocationDTO> toDTOList(List<UserLocation> locations);

    default UserLocationDTO toDetailDTO(UserLocation location) {
        if (location == null) {
            return null;
        }
        return UserLocationDTO.builder()
                .id(location.getId())
                .placeName(location.getPlaceName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .city(location.getCity())
                .country(location.getCountry())
                .postalCode(location.getPostalCode())
                .isPrimary(location.getIsPrimary())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
}
