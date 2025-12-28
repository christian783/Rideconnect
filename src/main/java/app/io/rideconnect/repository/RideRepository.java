package app.io.rideconnect.repository;

import app.io.rideconnect.entity.Ride;
import app.io.rideconnect.entity.enums.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, String> {

        List<Ride> findByPassengerId(String passengerId);

        Page<Ride> findByPassengerId(String passengerId, Pageable pageable);

        List<Ride> findByDriverId(String driverId);

        Page<Ride> findByDriverId(String driverId, Pageable pageable);

        List<Ride> findByStatus(RideStatus status);

        Page<Ride> findByStatus(RideStatus status, Pageable pageable);

        @Query("SELECT r FROM Ride r WHERE r.status IN :statuses")
        List<Ride> findByStatusIn(@Param("statuses") List<RideStatus> statuses);

        @Query("SELECT r FROM Ride r WHERE r.passenger.id = :passengerId AND r.status = :status")
        List<Ride> findByPassengerIdAndStatus(@Param("passengerId") String passengerId,
                        @Param("status") RideStatus status);

        @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId AND r.status = :status")
        List<Ride> findByDriverIdAndStatus(@Param("driverId") String driverId,
                        @Param("status") RideStatus status);

        @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
                        "AND r.requestedAt BETWEEN :startDate AND :endDate")
        List<Ride> findDriverRidesBetweenDates(@Param("driverId") String driverId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(r) FROM Ride r WHERE r.status = :status")
        Long countByStatus(@Param("status") RideStatus status);

        @Query("SELECT SUM(r.actualFare) FROM Ride r WHERE r.status = 'COMPLETED' " +
                        "AND r.completedAt BETWEEN :startDate AND :endDate")
        BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT AVG(r.actualFare) FROM Ride r WHERE r.status = 'COMPLETED'")
        BigDecimal getAverageFare();

        @Query("SELECT r FROM Ride r WHERE r.status = 'REQUESTED' " +
                        "AND r.requestedAt < :cutoffTime")
        List<Ride> findStaleRequestedRides(@Param("cutoffTime") LocalDateTime cutoffTime);

        @Query("SELECT r FROM Ride r " +
                        "WHERE r.status IN ('ACCEPTED', 'IN_PROGRESS') " +
                        "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(r.pickupLatitude)) * " +
                        "cos(radians(r.pickupLongitude) - radians(:longitude)) + " +
                        "sin(radians(:latitude)) * sin(radians(r.pickupLatitude)))) <= :radiusKm")
        List<Ride> findActiveRidesNearLocation(
                        @Param("latitude") double latitude,
                        @Param("longitude") double longitude,
                        @Param("radiusKm") double radiusKm);

        @Query("SELECT r FROM Ride r WHERE r.status IN :statuses " +
                        "AND r.requestedAt BETWEEN :startDate AND :endDate")
        List<Ride> findByStatusAndDateRange(
                        @Param("statuses") List<RideStatus> statuses,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

}
