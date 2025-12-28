package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.DriverTier;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final UserRepository userRepository;
    private final DriverProfileMapper driverProfileMapper;

    @Override
    public DriverProfileResponseDTO createDriverProfile(DriverProfileRequestDTO requestDTO) {
        log.info("Creating driver profile for user ID: {}", requestDTO.getUserId());

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (driverProfileRepository.existsByLicenseNumber(requestDTO.getLicenseNumber())) {
            throw new ResourceAlreadyExistsException("License number already registered");
        }

        if (driverProfileRepository.existsByVehicleRegistration(requestDTO.getVehicleRegistration())) {
            throw new ResourceAlreadyExistsException("Vehicle registration already registered");
        }

        DriverProfile driverProfile = driverProfileMapper.toEntity(requestDTO);
        driverProfile.setUser(user);
        driverProfile.setIsVerified(false);
        driverProfile.setTier(DriverTier.BRONZE);

        DriverProfile saved = driverProfileRepository.save(driverProfile);
        log.info("Driver profile created with ID: {}", saved.getId());

        return driverProfileMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverProfileResponseDTO getDriverProfileById(String id) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));
        return driverProfileMapper.toResponseDTO(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverProfileResponseDTO getDriverProfileByUserId(String userId) {
        DriverProfile profile = driverProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user"));
        return driverProfileMapper.toResponseDTO(profile);
    }

    @Override
    public DriverProfileResponseDTO updateDriverProfile(String id, DriverProfileRequestDTO requestDTO) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        // Update fields
        profile.setVehicleModel(requestDTO.getVehicleModel());
        profile.setVehicleColor(requestDTO.getVehicleColor());
        profile.setVehicleYear(requestDTO.getVehicleYear());

        DriverProfile updated = driverProfileRepository.save(profile);
        return driverProfileMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteDriverProfile(String id) {
        if (!driverProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver profile not found");
        }
        driverProfileRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverProfileResponseDTO> getAvailableDrivers() {
        return driverProfileRepository.findAvailableAndVerifiedDrivers().stream()
                .map(driverProfileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverProfileResponseDTO> getNearbyDrivers(LocationDTO location, double radiusKm) {
//        var point = driverProfileMapper.pointToLocationDTO(null); // Use mapper utility
        return driverProfileRepository.findNearbyAvailableDrivers(
                        0.0, radiusKm * 1000, 10) // Convert km to meters
                .stream()
                .map(driverProfileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DriverProfileResponseDTO verifyDriver(String id) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        profile.setIsVerified(true);
        DriverProfile updated = driverProfileRepository.save(profile);
        log.info("Driver verified: {}", id);

        return driverProfileMapper.toResponseDTO(updated);
    }

    @Override
    public DriverProfileResponseDTO updateDriverAvailability(String id, boolean isAvailable) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        profile.setIsAvailable(isAvailable);
        DriverProfile updated = driverProfileRepository.save(profile);

        return driverProfileMapper.toResponseDTO(updated);
    }

    @Override
    public DriverProfileResponseDTO updateDriverLocation(String id, LocationDTO location) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        // Convert LocationDTO to Point (implementation depends on your mapper)
        DriverProfile updated = driverProfileRepository.save(profile);

        return driverProfileMapper.toResponseDTO(updated);
    }

    @Override
    public DriverProfileResponseDTO updateDriverTier(String id, String tier) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        profile.setTier(DriverTier.valueOf(tier.toUpperCase()));
        DriverProfile updated = driverProfileRepository.save(profile);

        return driverProfileMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverAnalyticsDTO getDriverAnalytics() {
        return DriverAnalyticsDTO.builder()
                .totalDrivers(driverProfileRepository.count())
                .activeDrivers((long) driverProfileRepository.findByIsAvailableTrue().size())
                .verifiedDrivers((long) driverProfileRepository.findByIsVerifiedTrue().size())
                .bronzeDrivers(driverProfileRepository.countByTier(DriverTier.BRONZE).intValue())
                .silverDrivers(driverProfileRepository.countByTier(DriverTier.SILVER).intValue())
                .goldDrivers(driverProfileRepository.countByTier(DriverTier.GOLD).intValue())
                .platinumDrivers(driverProfileRepository.countByTier(DriverTier.PLATINUM).intValue())
                .build();
    }
}