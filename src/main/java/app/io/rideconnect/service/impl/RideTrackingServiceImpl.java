package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RideTrackingServiceImpl implements RideTrackingService {

    private final RideTrackingRepository rideTrackingRepository;
    private final RideRepository rideRepository;
    private final RideTrackingMapper rideTrackingMapper;

    @Override
    public void addTrackingPoint(String rideId, LocationDTO location, Double speed, Double heading) {
        log.debug("Adding tracking point for ride: {}", rideId);

        if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
            throw new IllegalArgumentException("Location with latitude and longitude is required");
        }

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    log.error("Ride not found with id: {}", rideId);
                    return new ResourceNotFoundException("Ride not found with id: " + rideId);
                });

        RideTracking tracking = RideTracking.builder()
                .ride(ride)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .timestamp(LocalDateTime.now())
                .speed(speed)
                .heading(heading)
                .build();

        RideTracking savedTracking = rideTrackingRepository.save(tracking);
        log.info("Tracking point added for ride: {} at lat: {}, long: {}", rideId, location.getLatitude(),
                location.getLongitude());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> getRideTrackingHistory(String rideId) {
        log.debug("Fetching tracking history for ride: {}", rideId);

        if (!rideRepository.existsById(rideId)) {
            log.error("Ride not found with id: {}", rideId);
            throw new ResourceNotFoundException("Ride not found with id: " + rideId);
        }

        List<RideTracking> trackingPoints = rideTrackingRepository.findByRideIdOrderByTimestampAsc(rideId);
        log.debug("Found {} tracking points for ride: {}", trackingPoints.size(), rideId);

        return trackingPoints.stream()
                .map(tracking -> LocationDTO.builder()
                        .latitude(tracking.getLatitude())
                        .longitude(tracking.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideTracking> getLatestTrackingPoint(String rideId) {
        log.debug("Fetching latest tracking point for ride: {}", rideId);

        if (!rideRepository.existsById(rideId)) {
            log.error("Ride not found with id: {}", rideId);
            throw new ResourceNotFoundException("Ride not found with id: " + rideId);
        }

        return rideTrackingRepository.findByRideIdOrderByTimestampAsc(rideId);
    }

    /**
     * Get tracking points as DTOs for API responses
     */
    public List<RideTrackingDTO> getRideTrackingHistoryAsDTO(String rideId) {
        log.debug("Fetching tracking history as DTO for ride: {}", rideId);

        if (!rideRepository.existsById(rideId)) {
            throw new ResourceNotFoundException("Ride not found with id: " + rideId);
        }

        List<RideTracking> trackingPoints = rideTrackingRepository.findByRideIdOrderByTimestampAsc(rideId);
        return trackingPoints.stream()
                .map(rideTrackingMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculate average speed from tracking points
     */
    public Double calculateAverageSpeed(String rideId) {
        log.debug("Calculating average speed for ride: {}", rideId);

        List<RideTracking> trackingPoints = rideTrackingRepository.findByRideIdOrderByTimestampAsc(rideId);

        if (trackingPoints.isEmpty()) {
            log.warn("No tracking points found for ride: {}", rideId);
            return 0.0;
        }

        double totalSpeed = trackingPoints.stream()
                .filter(t -> t.getSpeed() != null)
                .mapToDouble(t -> t.getSpeed())
                .sum();

        long speedCount = trackingPoints.stream()
                .filter(t -> t.getSpeed() != null)
                .count();

        if (speedCount == 0) {
            return 0.0;
        }

        double averageSpeed = totalSpeed / speedCount;
        log.info("Average speed for ride {}: {} km/h", rideId, averageSpeed);
        return averageSpeed;
    }
}