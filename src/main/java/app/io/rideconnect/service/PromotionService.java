package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import java.util.List;

public interface PromotionService {

    PromotionResponseDTO createPromotion(PromotionRequestDTO requestDTO);

    PromotionResponseDTO getPromotionById(String id);

    PromotionResponseDTO getPromotionByCode(String promoCode);

    PromotionResponseDTO updatePromotion(String id, PromotionRequestDTO requestDTO);

    void deletePromotion(String id);

    List<PromotionResponseDTO> getActivePromotions();

    List<PromotionResponseDTO> getAvailablePromotions();

    PromotionResponseDTO applyPromotion(String promoCode, java.math.BigDecimal orderAmount);

    java.math.BigDecimal calculateDiscount(String promoCode, java.math.BigDecimal orderAmount);

    void incrementPromotionUsage(String promotionId);

    void deactivateExpiredPromotions();
}
