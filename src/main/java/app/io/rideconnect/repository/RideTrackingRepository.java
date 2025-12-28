package app.io.rideconnect.repository;

import app.io.rideconnect.entity.RideTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideTrackingRepository extends JpaRepository<RideTracking, String> {

    List<RideTracking> findByRideIdOrderByTimestampAsc(String rideId);

    @Query("SELECT rt FROM RideTracking rt WHERE rt.ride.id = :rideId " +
            "AND rt.timestamp BETWEEN :startTime AND :endTime " +
            "ORDER BY rt.timestamp ASC")
    List<RideTracking> findByRideIdAndTimeBetween(@Param("rideId") String rideId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT * FROM ride_tracking rt " +
            "WHERE rt.ride_id = :rideId " +
            "ORDER BY rt.timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<RideTracking> findLatestTrackingPoint(@Param("rideId") String rideId);
}
