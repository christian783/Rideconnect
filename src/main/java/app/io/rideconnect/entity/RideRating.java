package app.io.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRating {

    @Id
    @Column( name = "id", columnDefinition = "varchar(36)")
    @UuidGenerator
    private String id;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(nullable = false)
    private Integer passengerRating; // 1-5

    @Column(nullable = false)
    private Integer driverRating; // 1-5

    private String passengerComment;
    private String driverComment;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
