package app.io.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideTracking {

    @Id
    @Column(name = "id", columnDefinition = "varchar(36)")
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    private LocalDateTime timestamp;
    private Double speed;
    private Double heading;
}