package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusinessProfileServiceImpl implements BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;
    private final UserRepository userRepository;
    private final BusinessProfileMapper businessProfileMapper;

    @Override
    public BusinessProfileResponseDTO createBusinessProfile(BusinessProfileRequestDTO requestDTO) {
        log.info("Creating business profile for user ID: {}", requestDTO.getUserId());

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (requestDTO.getTaxId() != null && businessProfileRepository.existsByTaxId(requestDTO.getTaxId())) {
            throw new ResourceAlreadyExistsException("Business with this Tax ID already exists");
        }

        BusinessProfile profile = businessProfileMapper.toEntity(requestDTO);
        profile.setUser(user);
        profile.setWalletBalance(BigDecimal.ZERO);

        BusinessProfile saved = businessProfileRepository.save(profile);
        log.info("Business profile created with ID: {}", saved.getId());

        return businessProfileMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileResponseDTO getBusinessProfileById(String id) {
        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found"));
        return businessProfileMapper.toResponseDTO(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileResponseDTO getBusinessProfileByUserId(String userId) {
        BusinessProfile profile = businessProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found for user"));
        return businessProfileMapper.toResponseDTO(profile);
    }

    @Override
    public BusinessProfileResponseDTO updateBusinessProfile(String id, BusinessProfileRequestDTO requestDTO) {
        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found"));

        profile.setBusinessName(requestDTO.getBusinessName());
        profile.setBusinessAddress(requestDTO.getBusinessAddress());

        BusinessProfile updated = businessProfileRepository.save(profile);
        return businessProfileMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteBusinessProfile(String id) {
        if (!businessProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Business profile not found");
        }
        businessProfileRepository.deleteById(id);
        log.info("Business profile deleted with ID: {}", id);
    }

    @Override
    public BusinessProfileResponseDTO updateWalletBalance(String id, BigDecimal amount) {
        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found"));

        BigDecimal newBalance = profile.getWalletBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOperationException("Insufficient wallet balance");
        }

        profile.setWalletBalance(newBalance);
        BusinessProfile updated = businessProfileRepository.save(profile);

        log.info("Wallet balance updated for business {}: {}", id, newBalance);
        return businessProfileMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessProfileResponseDTO> searchBusinessByName(String name) {
        return businessProfileRepository.findByBusinessNameContainingIgnoreCase(name).stream()
                .map(businessProfileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}