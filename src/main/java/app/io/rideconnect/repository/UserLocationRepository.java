package app.io.rideconnect.repository;

import app.io.rideconnect.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, String> {

    List<UserLocation> findByUserId(String userId);

    List<UserLocation> findByUserIdAndIsPrimary(String userId, Boolean isPrimary);

    Optional<UserLocation> findByIdAndUserId(String id, String userId);
}