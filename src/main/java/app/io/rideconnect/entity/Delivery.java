package app.io.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @Column( name = "id", columnDefinition = "varchar(36)")
    @UuidGenerator
    private String id;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    private String packageDescription;
    private String packageWeight;
    private String packageDimensions;

    private String recipientName;
    private String recipientPhone;

    private String proofOfDeliveryUrl;
    private String recipientSignatureUrl;

    @Column(length = 1000)
    private String deliveryNotes;

    private LocalDateTime deliveredAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
