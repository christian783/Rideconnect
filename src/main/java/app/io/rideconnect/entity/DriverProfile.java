package app.io.rideconnect.entity;

import app.io.rideconnect.entity.enums.DriverTier;
import app.io.rideconnect.entity.enums.VehicleType;
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
@Table(name = "driver_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "varchar(36)")
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private String licenseImageUrl;

    @Column(nullable = false, unique = true)
    private String vehicleRegistration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType; // MOTORCYCLE, CAR, VAN

    private String vehicleModel;
    private String vehicleColor;
    private Integer vehicleYear;

    @Column(nullable = false)
    private Boolean isVerified = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverTier tier; // BRONZE, SILVER, GOLD, PLATINUM

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    private Integer totalRides = Integer.valueOf(0);

    @Column(nullable = false)
    private Boolean isAvailable = Boolean.FALSE;

    @Column(name = "last_known_latitude")
    private Double lastKnownLatitude;

    @Column(name = "last_known_longitude")
    private Double lastKnownLongitude;

    private String referralCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "driver")
    private Set<DriverFinance> finances = new HashSet<>();
}