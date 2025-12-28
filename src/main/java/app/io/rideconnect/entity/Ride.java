package app.io.rideconnect.entity;

import app.io.rideconnect.entity.enums.PaymentMethod;
import app.io.rideconnect.entity.enums.PaymentStatus;
import app.io.rideconnect.entity.enums.RideStatus;
import app.io.rideconnect.entity.enums.RideType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "varchar(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideType rideType; // PASSENGER, GOODS_DELIVERY, CARPOOL

    @Column(name = "pickup_latitude", nullable = false)
    private Double pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false)
    private Double pickupLongitude;

    @Column(name = "dropoff_latitude", nullable = false)
    private Double dropoffLatitude;

    @Column(name = "dropoff_longitude", nullable = false)
    private Double dropoffLongitude;

    private String pickupAddress;
    private String dropoffAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status; // REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(precision = 19, scale = 2)
    private BigDecimal estimatedFare;

    @Column(precision = 19, scale = 2)
    private BigDecimal actualFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceKm;

    private Integer estimatedDurationMinutes;
    private Integer actualDurationMinutes;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CASH, MOBILE_MONEY, CARD

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PENDING, COMPLETED, FAILED

    private Boolean isCarpoolEnabled = false;
    private Integer carpoolSeatsAvailable;

    private Boolean sosActivated = false;
    private String sosNotes;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private RideRating rating;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private Set<RideTracking> trackingPoints = new HashSet<>();
}