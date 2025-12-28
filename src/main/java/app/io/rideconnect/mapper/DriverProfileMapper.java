package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface DriverProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "tier", constant = "BRONZE")
    @Mapping(target = "rating", constant = "0")
    @Mapping(target = "totalRides", constant = "0")
    @Mapping(target = "isAvailable", constant = "false")
    // @Mapping(target = "lastKnownLocation", ignore = true)
    @Mapping(target = "finances", ignore = true)
    DriverProfile toEntity(DriverProfileRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    // @Mapping(target = "lastKnownLocation", source = "lastKnownLocation",
    // qualifiedByName = "driverPointToLocationDTO")
    @Mapping(target = "vehicleType", source = "vehicleType", qualifiedByName = "driverEnumToString")
    @Mapping(target = "tier", source = "tier", qualifiedByName = "driverEnumToString")
    DriverProfileResponseDTO toResponseDTO(DriverProfile entity);

    @Named("driverPointToLocationDTO")
    default LocationDTO driverToLocationDTO(DriverProfile driver) {
        if (driver == null || driver.getLastKnownLatitude() == null)
            return null;
        return LocationDTO.builder()
                .latitude(driver.getLastKnownLatitude())
                .longitude(driver.getLastKnownLongitude())
                .build();
    }

    @Named("driverEnumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }
}
