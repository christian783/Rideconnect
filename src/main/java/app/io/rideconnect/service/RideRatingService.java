package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;


public interface RideRatingService {

    RideRatingResponseDTO createRating(RideRatingRequestDTO requestDTO);

    RideRatingResponseDTO getRatingByRideId(String rideId);

    java.math.BigDecimal getAverageDriverRating(String driverId);

    java.math.BigDecimal getAveragePassengerRating(String passengerId);

    void updateDriverRating (String driverId);
}