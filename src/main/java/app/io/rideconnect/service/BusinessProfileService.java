package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import java.util.List;

public interface BusinessProfileService {

    BusinessProfileResponseDTO createBusinessProfile(BusinessProfileRequestDTO requestDTO);

    BusinessProfileResponseDTO getBusinessProfileById(String id);

    BusinessProfileResponseDTO getBusinessProfileByUserId(String userId);

    BusinessProfileResponseDTO updateBusinessProfile(String id, BusinessProfileRequestDTO requestDTO);

    void deleteBusinessProfile(String id);

    BusinessProfileResponseDTO updateWalletBalance(String id, java.math.BigDecimal amount);

    List<BusinessProfileResponseDTO> searchBusinessByName(String name);
}