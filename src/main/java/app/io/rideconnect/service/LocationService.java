package app.io.rideconnect.service;

import app.io.rideconnect.dto.LocationDTO;
import app.io.rideconnect.dto.UserLocationDTO;
import app.io.rideconnect.exception.LocationNotFoundException;
import app.io.rideconnect.exception.UserNotFoundException;

import java.util.List;

public interface LocationService {
    /**
     * Create a new location for a user
     */
    UserLocationDTO createLocation(String userId, LocationDTO locationDTO) throws UserNotFoundException;

    /**
     * Get all locations for a user
     */
    List<UserLocationDTO> getUserLocations(String userId) throws UserNotFoundException;

    /**
     * Update an existing location
     */
    UserLocationDTO updateLocation(String userId, String locationId, LocationDTO locationDTO)
            throws UserNotFoundException, LocationNotFoundException;

    /**
     * Delete a location
     */
    void deleteLocation(String userId, String locationId) throws UserNotFoundException, LocationNotFoundException;
}