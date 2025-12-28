package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { UserMapper.class, DriverProfileMapper.class })
public interface RideMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "status", constant = "REQUESTED")
    @Mapping(target = "pickupLatitude", source = "pickupLocation.latitude")
    @Mapping(target = "pickupLongitude", source = "pickupLocation.longitude")
    @Mapping(target = "dropoffLatitude", source = "dropoffLocation.latitude")
    @Mapping(target = "dropoffLongitude", source = "dropoffLocation.longitude")
    @Mapping(target = "requestedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "sosActivated", constant = "false")
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "trackingPoints", ignore = true)
    @Mapping(target = "paymentStatus", constant = "PENDING")
    @Mapping(target = "acceptedAt", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    Ride toEntity(RideRequestDTO dto);

    @Mapping(target = "passengerId", source = "passenger.id")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "pickupLocation", source = ".", qualifiedByName = "rideToPickupLocation")
    @Mapping(target = "dropoffLocation", source = ".", qualifiedByName = "rideToDropoffLocation")
    @Mapping(target = "rideType", source = "rideType", qualifiedByName = "enumToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "enumToString")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "enumToString")
    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "enumToString")
    @Mapping(target = "passenger", source = "passenger")
    @Mapping(target = "driver", source = "driver.driverProfile")
    @Mapping(target = "deliveryDetails", ignore = true)
    RideResponseDTO toResponseDTO(Ride entity);

    @Named("rideToPickupLocation")
    default LocationDTO rideToPickupLocation(Ride ride) {
        if (ride == null || ride.getPickupLatitude() == null || ride.getPickupLongitude() == null) {
            return null;
        }
        return LocationDTO.builder()
                .latitude(ride.getPickupLatitude())
                .longitude(ride.getPickupLongitude())
                .address(ride.getPickupAddress())
                .build();
    }

    @Named("rideToDropoffLocation")
    default LocationDTO rideToDropoffLocation(Ride ride) {
        if (ride == null || ride.getDropoffLatitude() == null || ride.getDropoffLongitude() == null) {
            return null;
        }
        return LocationDTO.builder()
                .latitude(ride.getDropoffLatitude())
                .longitude(ride.getDropoffLongitude())
                .address(ride.getDropoffAddress())
                .build();
    }

    @Named("enumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }
}
