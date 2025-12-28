package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final DriverProfileRepository driverProfileRepository;
    private final NotificationService notificationService;

    @Override
    public String findBestDriver(LocationDTO pickupLocation, String vehicleType) {
        log.debug("Finding best driver for location: lat={}, lon={}, vehicle type: {}",
                pickupLocation.getLatitude(), pickupLocation.getLongitude(), vehicleType);

        if (pickupLocation == null || pickupLocation.getLatitude() == null || pickupLocation.getLongitude() == null) {
            throw new IllegalArgumentException("Valid pickup location with latitude and longitude is required");
        }

        try {
            // Get available drivers - filtered by vehicle type if provided
            List<DriverProfile> availableDrivers = driverProfileRepository.findAll().stream()
                    .filter(DriverProfile::getIsAvailable)
                    .filter(driver -> vehicleType == null || driver.getVehicleType().toString().equals(vehicleType))
                    .collect(Collectors.toList());

            if (availableDrivers.isEmpty()) {
                log.warn("No available drivers found for location");
                return null;
            }

            // Filter drivers within 5km radius and sort by rating
            DriverProfile bestDriver = availableDrivers.stream()
                    .filter(driver -> isWithinRadius(pickupLocation, driver, 5.0))
                    .max(Comparator.comparing(DriverProfile::getRating))
                    .orElse(null);

            if (bestDriver != null) {
                log.info("Best driver found: {} with rating: {}", bestDriver.getId(), bestDriver.getRating());
            } else {
                log.warn("No driver found within 5km radius");
            }

            return bestDriver != null ? bestDriver.getId() : null;
        } catch (Exception e) {
            log.error("Error finding best driver", e);
            throw new RuntimeException("Error finding best driver: " + e.getMessage());
        }
    }

    @Override
    public List<String> findNearbyDrivers(LocationDTO location, double radiusKm, String vehicleType) {
        log.debug("Finding nearby drivers for location: lat={}, lon={}, radius: {}km, vehicle type: {}",
                location.getLatitude(), location.getLongitude(), radiusKm, vehicleType);

        if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
            throw new IllegalArgumentException("Valid location with latitude and longitude is required");
        }

        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }

        try {
            List<DriverProfile> nearbyDrivers = driverProfileRepository.findAll().stream()
                    .filter(DriverProfile::getIsAvailable)
                    .filter(driver -> vehicleType == null || driver.getVehicleType().toString().equals(vehicleType))
                    .filter(driver -> isWithinRadius(location, driver, radiusKm))
                    .sorted(Comparator.comparing(DriverProfile::getRating).reversed())
                    .limit(20)
                    .collect(Collectors.toList());

            log.info("Found {} nearby drivers within {}km radius", nearbyDrivers.size(), radiusKm);

            return nearbyDrivers.stream()
                    .map(DriverProfile::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding nearby drivers", e);
            throw new RuntimeException("Error finding nearby drivers: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void notifyNearbyDrivers(String rideId, LocationDTO location, double radiusKm) {
        log.info("Notifying nearby drivers about ride: {} at location: lat={}, lon={}",
                rideId, location.getLatitude(), location.getLongitude());

        try {
            List<String> driverIds = findNearbyDrivers(location, radiusKm, null);

            if (driverIds.isEmpty()) {
                log.warn("No drivers found to notify for ride: {}", rideId);
                return;
            }

            log.info("Notifying {} nearby drivers about ride: {}", driverIds.size(), rideId);

            driverIds.forEach(driverId -> {
                try {
                    notificationService.sendRideRequestNotification(driverId, rideId);
                    log.debug("Notification sent to driver: {} for ride: {}", driverId, rideId);
                } catch (Exception e) {
                    log.error("Error sending notification to driver: {}", driverId, e);
                }
            });
        } catch (Exception e) {
            log.error("Error notifying nearby drivers for ride: {}", rideId, e);
        }
    }

    /**
     * Calculate if a driver is within the specified radius of a location
     * Uses Haversine formula for distance calculation
     */
    private boolean isWithinRadius(LocationDTO location, DriverProfile driver, double radiusKm) {
        if (driver.getLastKnownLatitude() == null || driver.getLastKnownLongitude() == null) {
            log.debug("Driver {} has no last known location", driver.getId());
            return false;
        }

        double distance = calculateDistance(
                location.getLatitude(),
                location.getLongitude(),
                driver.getLastKnownLatitude(),
                driver.getLastKnownLongitude());

        return distance <= radiusKm;
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}