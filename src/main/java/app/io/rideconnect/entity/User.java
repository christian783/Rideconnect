package app.io.rideconnect.entity;

import app.io.rideconnect.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "varchar(36)")
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String resetToken;

    @Column()
    private LocalDateTime resetTokenExpiry;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLocation> locations = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_location_id", referencedColumnName = "id")
    private UserLocation currentLocation; // Reference to primary location

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "app_user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", columnDefinition = "varchar(36)" // 👈
                                                                                                                                                  // force
                                                                                                                                                  // varchar(36)
    ), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", columnDefinition = "varchar(36)"))
    private Set<Role> roles = new HashSet();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status; // ACTIVE, SUSPENDED, INACTIVE

    @Column()
    private String profileImageUrl;

    @Column()
    private String preferredLanguage; // en, fr, rw

    @Column()
    private String preferredCurrency; // RWF, USD

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private DriverProfile driverProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private BusinessProfile businessProfile;

    @OneToMany(mappedBy = "passenger")
    private Set<Ride> ridesAsPassenger = new HashSet<>();

    @OneToMany(mappedBy = "driver")
    private Set<Ride> ridesAsDriver = new HashSet<>();

    // Add these fields to your existing User entity
    @Column()
    private Boolean emailVerified = Boolean.FALSE;

    @Column()
    private Boolean phoneVerified = Boolean.FALSE;

    @Column(name = "account_locked", nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean accountLocked = Boolean.FALSE;

    private Integer failedLoginAttempts = 0;

    @Column()
    private LocalDateTime lastLoginAt;

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addLocation(UserLocation location) {
        location.setUser(this);
        this.locations.add(location);
        if (location.getIsPrimary()) {
            this.currentLocation = location;
        }
    }

    public void removeLocation(UserLocation location) {
        this.locations.remove(location);
        location.setUser(null);
        if (this.currentLocation != null && this.currentLocation.equals(location)) {
            this.currentLocation = null;
        }
    }

    public void setPrimaryLocation(UserLocation location) {
        // Set all locations to non-primary
        this.locations.forEach(loc -> loc.setIsPrimary(false));

        // Set the selected location as primary
        location.setIsPrimary(true);
        this.currentLocation = location;

        // Ensure the location is in the user's locations
        if (!this.locations.contains(location)) {
            this.addLocation(location);
        }
    }
}