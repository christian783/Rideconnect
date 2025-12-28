package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RideService {

    RideResponseDTO createRide(RideRequestDTO requestDTO);

    RideResponseDTO getRideById(String id);

    RideResponseDTO acceptRide(String rideId, String driverId);

    RideResponseDTO startRide(String rideId);

    RideResponseDTO completeRide(String rideId);

    RideResponseDTO cancelRide(String rideId, String reason);

    RideResponseDTO updateRideStatus(String rideId, String status);

    PageResponseDTO<RideResponseDTO> getPassengerRides(String passengerId, Pageable pageable);

    PageResponseDTO<RideResponseDTO> getDriverRides(String driverId, Pageable pageable);

    List<RideResponseDTO> getActiveRides();

    List<RideResponseDTO> getRidesByStatus(String status);

    RideResponseDTO calculateFare(RideRequestDTO requestDTO);

    RideResponseDTO activateSOS(String rideId, String notes);

    RideAnalyticsDTO getRideAnalytics();

    RideAnalyticsDTO getRideAnalyticsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}