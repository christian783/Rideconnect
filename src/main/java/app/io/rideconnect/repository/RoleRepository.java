package app.io.rideconnect.repository;

import app.io.rideconnect.entity.Role;
import app.io.rideconnect.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(UserRole name);
    Boolean existsByName(UserRole name);

}
