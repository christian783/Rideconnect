package app.io.rideconnect.repository;

import app.io.rideconnect.entity.DriverProfile;
import app.io.rideconnect.entity.enums.DriverTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, String> {

        Optional<DriverProfile> findByUserId(String userId);

        Optional<DriverProfile> findByLicenseNumber(String licenseNumber);

        Optional<DriverProfile> findByVehicleRegistration(String vehicleRegistration);

        boolean existsByLicenseNumber(String licenseNumber);

        boolean existsByVehicleRegistration(String vehicleRegistration);

        List<DriverProfile> findByIsAvailableTrue();

        List<DriverProfile> findByIsVerifiedTrue();

        List<DriverProfile> findByTier(DriverTier tier);

        @Query("SELECT dp FROM DriverProfile dp WHERE dp.isAvailable = true AND dp.isVerified = true")
        List<DriverProfile> findAvailableAndVerifiedDrivers();

        @Query("SELECT dp FROM DriverProfile dp " +
                        "WHERE dp.isAvailable = true AND dp.isVerified = true " +
                        "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(dp.lastKnownLatitude)) * " +
                        "cos(radians(dp.lastKnownLongitude) - radians(:longitude)) + " +
                        "sin(radians(:latitude)) * sin(radians(dp.lastKnownLatitude)))) <= :radiusKm " +
                        "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(dp.lastKnownLatitude)) * " +
                        "cos(radians(dp.lastKnownLongitude) - radians(:longitude)) + " +
                        "sin(radians(:latitude)) * sin(radians(dp.lastKnownLatitude))))")
        List<DriverProfile> findNearbyAvailableDrivers(
                        @Param("latitude") double latitude,
                        @Param("longitude") double longitude,
                        @Param("radiusKm") double radiusKm);

        @Query("SELECT COUNT(dp) FROM DriverProfile dp WHERE dp.tier = :tier")
        Long countByTier(@Param("tier") DriverTier tier);

        @Query("SELECT AVG(dp.rating) FROM DriverProfile dp WHERE dp.totalRides > 0")
        BigDecimal getAverageRating();
}