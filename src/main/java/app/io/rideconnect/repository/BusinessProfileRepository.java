package app.io.rideconnect.repository;

import app.io.rideconnect.entity.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, String> {

    Optional<BusinessProfile> findByUserId(String userId);

    Optional<BusinessProfile> findByTaxId(String taxId);

    boolean existsByTaxId(String taxId);

    List<BusinessProfile> findByBusinessNameContainingIgnoreCase(String businessName);
}