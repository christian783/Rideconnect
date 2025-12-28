package app.io.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotspots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotspot {
    @Id
    @Column(name = "id", columnDefinition = "varchar(36)")
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    private Double radiusMeters;

    @Column(precision = 5, scale = 2)
    private BigDecimal demandScore; // 0-100

    private Integer averageRidesPerHour;

    private String timeOfDayPattern; // JSON or string representing peak hours

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
