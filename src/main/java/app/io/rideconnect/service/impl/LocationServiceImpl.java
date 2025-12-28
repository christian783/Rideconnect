package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.LocationDTO;
import app.io.rideconnect.dto.UserLocationDTO;
import app.io.rideconnect.entity.User;
import app.io.rideconnect.entity.UserLocation;
import app.io.rideconnect.exception.LocationNotFoundException;
import app.io.rideconnect.exception.UserNotFoundException;
import app.io.rideconnect.mapper.LocationMapper;
import app.io.rideconnect.repository.UserLocationRepository;
import app.io.rideconnect.repository.UserRepository;
import app.io.rideconnect.service.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationServiceImpl implements LocationService {

    private final UserLocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LocationMapper locationMapper;

    @Override
    public UserLocationDTO createLocation(String userId, LocationDTO locationDTO) throws UserNotFoundException {
        log.debug("Creating location for user: {}", userId);

        if (locationDTO == null) {
            throw new IllegalArgumentException("LocationDTO cannot be null");
        }

        if (locationDTO.getLatitude() == null || locationDTO.getLongitude() == null) {
            throw new IllegalArgumentException("Latitude and longitude are required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        UserLocation location = locationMapper.toEntity(locationDTO);
        location.setUser(user);

        // If this is the first location or marked as primary, set it as primary
        if (location.getIsPrimary() == null || location.getIsPrimary()) {
            location.setIsPrimary(true);
            user.getLocations().forEach(loc -> loc.setIsPrimary(false));
            log.debug("Setting location as primary for user: {}", userId);
        } else {
            location.setIsPrimary(false);
        }

        UserLocation savedLocation = locationRepository.save(location);
        user.getLocations().add(savedLocation);

        // Update user's current location if this is primary
        if (savedLocation.getIsPrimary()) {
            user.setCurrentLocation(savedLocation);
            userRepository.save(user);
            log.debug("Updated current location for user: {}", userId);
        }

        log.info("Location created successfully for user: {}", userId);
        return locationMapper.toDetailDTO(savedLocation);
    }

    @Override
    public List<UserLocationDTO> getUserLocations(String userId) throws UserNotFoundException {
        log.debug("Fetching locations for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User not found with id: {}", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        List<UserLocation> locations = locationRepository.findByUserId(userId);
        log.debug("Found {} locations for user: {}", locations.size(), userId);

        return locations.stream()
                .map(locationMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing location
     */
    public UserLocationDTO updateLocation(String userId, String locationId, LocationDTO locationDTO)
            throws UserNotFoundException, LocationNotFoundException {
        log.debug("Updating location {} for user: {}", locationId, userId);

        if (locationDTO == null) {
            throw new IllegalArgumentException("LocationDTO cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserLocation location = locationRepository.findByIdAndUserId(locationId, userId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));

        // Update location fields
        if (locationDTO.getLatitude() != null) {
            location.setLatitude(locationDTO.getLatitude());
        }
        if (locationDTO.getLongitude() != null) {
            location.setLongitude(locationDTO.getLongitude());
        }
        if (locationDTO.getAddress() != null) {
            location.setAddress(locationDTO.getAddress());
        }
        if (locationDTO.getCity() != null) {
            location.setCity(locationDTO.getCity());
        }
        if (locationDTO.getCountry() != null) {
            location.setCountry(locationDTO.getCountry());
        }
        if (locationDTO.getPlaceName() != null) {
            location.setPlaceName(locationDTO.getPlaceName());
        }
        if (locationDTO.getPostalCode() != null) {
            location.setPostalCode(locationDTO.getPostalCode());
        }

        UserLocation updatedLocation = locationRepository.save(location);
        log.info("Location updated successfully: {}", locationId);

        return locationMapper.toDetailDTO(updatedLocation);
    }

    /**
     * Delete a location
     */
    public void deleteLocation(String userId, String locationId)
            throws UserNotFoundException, LocationNotFoundException {
        log.debug("Deleting location {} for user: {}", locationId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserLocation location = locationRepository.findByIdAndUserId(locationId, userId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));

        // Prevent deletion of primary location if it's the only location
        if (location.getIsPrimary() && user.getLocations().size() <= 1) {
            throw new IllegalStateException("Cannot delete the only primary location");
        }

        user.getLocations().remove(location);

        // If deleted location was primary, set first remaining location as primary
        if (location.getIsPrimary() && !user.getLocations().isEmpty()) {
            UserLocation firstLocation = user.getLocations().iterator().next();
            firstLocation.setIsPrimary(true);
            user.setCurrentLocation(firstLocation);
            userRepository.save(user);
            log.debug("Set new primary location for user: {}", userId);
        }

        locationRepository.delete(location);
        log.info("Location deleted successfully: {}", locationId);
    }
}