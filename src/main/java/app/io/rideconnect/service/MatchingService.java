package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import java.util.List;

public interface MatchingService {

    String findBestDriver(LocationDTO pickupLocation, String vehicleType);

    List<String> findNearbyDrivers(LocationDTO location, double radiusKm, String vehicleType);

    void notifyNearbyDrivers(String rideId, LocationDTO location, double radiusKm);
}