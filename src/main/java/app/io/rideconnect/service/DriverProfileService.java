package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import java.util.List;

public interface DriverProfileService {

    DriverProfileResponseDTO createDriverProfile(DriverProfileRequestDTO requestDTO);

    DriverProfileResponseDTO getDriverProfileById(String id);

    DriverProfileResponseDTO getDriverProfileByUserId(String userId);

    DriverProfileResponseDTO updateDriverProfile(String id, DriverProfileRequestDTO requestDTO);

    void deleteDriverProfile(String id);

    List<DriverProfileResponseDTO> getAvailableDrivers();

    List<DriverProfileResponseDTO> getNearbyDrivers(LocationDTO location, double radiusKm);

    DriverProfileResponseDTO verifyDriver(String id);

    DriverProfileResponseDTO updateDriverAvailability(String id, boolean isAvailable);

    DriverProfileResponseDTO updateDriverLocation(String id, LocationDTO location);

    DriverProfileResponseDTO updateDriverTier(String id, String tier);

    DriverAnalyticsDTO getDriverAnalytics();
}
