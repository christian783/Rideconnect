package app.io.rideconnect.repository;

import app.io.rideconnect.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {

    Optional<Delivery> findByRideId(String rideId);

    @Query("SELECT d FROM Delivery d WHERE d.ride.passenger.id = :userId")
    List<Delivery> findByUserId(@Param("userId") String userId);

    @Query("SELECT d FROM Delivery d WHERE d.deliveredAt IS NULL")
    List<Delivery> findPendingDeliveries();

    @Query("SELECT d FROM Delivery d WHERE d.deliveredAt BETWEEN :startDate AND :endDate")
    List<Delivery> findDeliveriesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}