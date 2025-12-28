package app.io.rideconnect.repository;

import app.io.rideconnect.entity.DriverFinance;
import app.io.rideconnect.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DriverFinanceRepository extends JpaRepository<DriverFinance, String> {

    List<DriverFinance> findByDriverId(String driverId);

    Page<DriverFinance> findByDriverId(String driverId, Pageable pageable);

    @Query("SELECT df FROM DriverFinance df WHERE df.driver.id = :driverId " +
            "ORDER BY df.transactionDate DESC")
    List<DriverFinance> findByDriverIdOrderByDateDesc(@Param("driverId") String driverId);

    @Query("SELECT df FROM DriverFinance df WHERE df.driver.id = :driverId " +
            "AND df.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY df.transactionDate DESC")
    List<DriverFinance> findByDriverIdAndDateBetween(@Param("driverId") String driverId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(df.amount) FROM DriverFinance df " +
            "WHERE df.driver.id = :driverId AND df.transactionType = :type")
    BigDecimal sumByDriverIdAndType(@Param("driverId") String driverId,
                                    @Param("type") TransactionType type);

    @Query("SELECT SUM(df.amount) FROM DriverFinance df " +
            "WHERE df.driver.id = :driverId AND df.transactionType = :type " +
            "AND df.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByDriverIdAndTypeAndDateBetween(@Param("driverId") String driverId,
                                                  @Param("type") TransactionType type,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT * FROM driver_finances df " +
            "WHERE df.driver_id = :driverId " +
            "ORDER BY df.transaction_date DESC LIMIT 1", nativeQuery = true)
    Optional<DriverFinance> findLatestTransaction(@Param("driverId") String driverId);
}