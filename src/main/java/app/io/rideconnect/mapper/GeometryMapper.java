package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.LocationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeometryMapper {

    default LocationDTO coordinatesToLocationDTO(double latitude, double longitude) {
        return LocationDTO.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }
}