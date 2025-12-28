package app.io.rideconnect.mapper;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.UserRole;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "currentLocation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "driverProfile", ignore = true)
    @Mapping(target = "businessProfile", ignore = true)
    @Mapping(target = "ridesAsPassenger", ignore = true)
    @Mapping(target = "ridesAsDriver", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "currentLocation", source = "currentLocation", qualifiedByName = "mapCurrentLocation")
    @Mapping(target = "locations", source = "locations", qualifiedByName = "mapUserLocations")
    @Mapping(target = "roles", expression = "java(mapRolesToRoleNames(entity.getRoles()))")
    @Mapping(target = "status", source = "status", qualifiedByName = "userEnumToString")
    UserResponseDTO toResponseDTO(User entity);

    void updateEntityFromDTO(UserUpdateDTO dto, @MappingTarget User entity);

    @Named("userPointToLocationDTO")
    default LocationDTO pointToLocationDTO(UserLocation location) {
        if (location == null)
            return null;
        return LocationDTO.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    @Named("userLocationToLocationDTO")
    default LocationDTO userLocationToLocationDTO(UserLocation location) {
        if (location == null)
            return null;
        return LocationDTO.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .city(location.getCity())
                .country(location.getCountry())
                .placeName(location.getPlaceName())
                .postalCode(location.getPostalCode())
                .isPrimary(location.getIsPrimary())
                .build();
    }

    @Named("userEnumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }

    // Remove @Named annotation and make it a regular default method
    default Set<String> mapRolesToRoleNames(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream()
                .map(Role::getName)
                .map(role -> role.name())
                .collect(Collectors.toSet());
    }

    default Set<Role> mapRoleNamesToRoles(Set<UserRole> roleNames) {
        if (roleNames == null)
            return null;
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }

    @Named("mapUserLocations")
    default List<UserLocationDTO> mapUserLocations(Set<UserLocation> locations) {
        if (locations == null || locations.isEmpty())
            return Collections.emptyList();

        return locations.stream()
                .map(loc -> UserLocationDTO.builder()
                        .id(loc.getId())
                        .placeName(loc.getPlaceName())
                        .latitude(loc.getLatitude())
                        .longitude(loc.getLongitude())
                        .address(loc.getAddress())
                        .city(loc.getCity())
                        .country(loc.getCountry())
                        .postalCode(loc.getPostalCode())
                        .isPrimary(loc.getIsPrimary())
                        .createdAt(loc.getCreatedAt())
                        .updatedAt(loc.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapCurrentLocation")
    default LocationDTO mapCurrentLocation(UserLocation location) {
        if (location == null)
            return null;
        return LocationDTO.builder()
                .placeName(location.getPlaceName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .city(location.getCity())
                .country(location.getCountry())
                .postalCode(location.getPostalCode())
                .isPrimary(location.getIsPrimary())
                .build();
    }
}