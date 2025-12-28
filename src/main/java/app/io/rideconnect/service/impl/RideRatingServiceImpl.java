package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.DriverTier;
import app.io.rideconnect.entity.enums.RideStatus;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RideRatingServiceImpl implements RideRatingService {

    private final RideRatingRepository rideRatingRepository;
    private final RideRepository rideRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final RideRatingMapper rideRatingMapper;

    @Override
    public RideRatingResponseDTO createRating(RideRatingRequestDTO requestDTO) {
        log.info("Creating rating for ride ID: {}", requestDTO.getRideId());

        Ride ride = rideRepository.findById(requestDTO.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot rate incomplete ride");
        }

        RideRating rating = rideRatingRepository.findByRideId(requestDTO.getRideId())
                .orElse(new RideRating());

        rating.setRide(ride);

        if ("PASSENGER".equalsIgnoreCase(requestDTO.getRaterType())) {
            rating.setPassengerRating(requestDTO.getRating());
            rating.setPassengerComment(requestDTO.getComment());
        } else if ("DRIVER".equalsIgnoreCase(requestDTO.getRaterType())) {
            rating.setDriverRating(requestDTO.getRating());
            rating.setDriverComment(requestDTO.getComment());
        }

        RideRating saved = rideRatingRepository.save(rating);

        // Update driver's overall rating
        if (ride.getDriver() != null && "PASSENGER".equalsIgnoreCase(requestDTO.getRaterType())) {
            updateDriverRating(ride.getDriver().getId());
        }

        log.info("Rating created/updated for ride: {}", requestDTO.getRideId());
        return rideRatingMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RideRatingResponseDTO getRatingByRideId(String rideId) {
        RideRating rating = rideRatingRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for ride"));
        return rideRatingMapper.toResponseDTO(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageDriverRating(String driverId) {
        BigDecimal avgRating = rideRatingRepository.getAverageDriverRating(driverId);
        return avgRating != null ? avgRating : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAveragePassengerRating(String passengerId) {
        BigDecimal avgRating = rideRatingRepository.getAveragePassengerRating(passengerId);
        return avgRating != null ? avgRating : BigDecimal.ZERO;
    }

    @Override
    public void updateDriverRating(String driverId) {
        BigDecimal avgRating = getAverageDriverRating(driverId);

        DriverProfile driverProfile = driverProfileRepository.findByUserId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        driverProfile.setRating(avgRating);

        // Update tier based on rating and rides
        updateDriverTierBasedOnPerformance(driverProfile);

        driverProfileRepository.save(driverProfile);
        log.info("Driver rating updated for driver {}: {}", driverId, avgRating);
    }

    private void updateDriverTierBasedOnPerformance(DriverProfile profile) {
        BigDecimal rating = profile.getRating();
        Integer totalRides = profile.getTotalRides();

        if (totalRides >= 500 && rating.compareTo(new BigDecimal("4.8")) >= 0) {
            profile.setTier(DriverTier.PLATINUM);
        } else if (totalRides >= 200 && rating.compareTo(new BigDecimal("4.5")) >= 0) {
            profile.setTier(DriverTier.GOLD);
        } else if (totalRides >= 50 && rating.compareTo(new BigDecimal("4.0")) >= 0) {
            profile.setTier(DriverTier.SILVER);
        } else {
            profile.setTier(DriverTier.BRONZE);
        }
    }
}