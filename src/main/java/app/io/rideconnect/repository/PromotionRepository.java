package app.io.rideconnect.repository;

import app.io.rideconnect.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, String> {

    Optional<Promotion> findByPromoCode(String promoCode);

    List<Promotion> findByIsActiveTrue();

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true " +
            "AND p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Promotion> findActivePromotions(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true " +
            "AND p.startDate <= :currentDate AND p.endDate >= :currentDate " +
            "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    List<Promotion> findAvailablePromotions(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :currentDate")
    List<Promotion> findExpiredPromotions(@Param("currentDate") LocalDateTime currentDate);
}