package app.io.rideconnect.repository;

import app.io.rideconnect.entity.RideRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface RideRatingRepository extends JpaRepository<RideRating, String> {

    Optional<RideRating> findByRideId(String rideId);

    @Query("SELECT AVG(rr.driverRating) FROM RideRating rr " +
            "JOIN rr.ride r WHERE r.driver.id = :driverId")
    BigDecimal getAverageDriverRating(@Param("driverId") String driverId);

    @Query("SELECT AVG(rr.passengerRating) FROM RideRating rr " +
            "JOIN rr.ride r WHERE r.passenger.id = :passengerId")
    BigDecimal getAveragePassengerRating(@Param("passengerId") String passengerId);

    @Query("SELECT COUNT(rr) FROM RideRating rr " +
            "JOIN rr.ride r WHERE r.driver.id = :driverId AND rr.driverRating >= :minRating")
    Long countDriverRatingsAbove(@Param("driverId") String driverId,
                                 @Param("minRating") Integer minRating);
}