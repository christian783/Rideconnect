package app.io.rideconnect.entity;

import app.io.rideconnect.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_finances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverFinance {
    @Id
    @Column(columnDefinition = "varchar(36)", name = "id")
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverProfile driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // INCOME, EXPENSE, COMMISSION, BONUS, FUEL

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    private String description;
    private String category; // RIDE_FARE, DELIVERY, MAINTENANCE, FUEL, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @CreationTimestamp
    private LocalDateTime transactionDate;
}