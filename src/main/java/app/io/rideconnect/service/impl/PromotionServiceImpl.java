package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.PromotionRequestDTO;
import app.io.rideconnect.dto.PromotionResponseDTO;
import app.io.rideconnect.entity.Promotion;
import app.io.rideconnect.entity.enums.DiscountType;
import app.io.rideconnect.exception.InvalidOperationException;
import app.io.rideconnect.exception.ResourceAlreadyExistsException;
import app.io.rideconnect.exception.ResourceNotFoundException;
import app.io.rideconnect.mapper.PromotionMapper;
import app.io.rideconnect.repository.PromotionRepository;
import app.io.rideconnect.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionResponseDTO createPromotion(PromotionRequestDTO requestDTO) {
        log.info("Creating promotion with code: {}", requestDTO.getPromoCode());

        if (promotionRepository.findByPromoCode(requestDTO.getPromoCode()).isPresent()) {
            throw new ResourceAlreadyExistsException("Promotion code already exists");
        }

        Promotion promotion = promotionMapper.toEntity(requestDTO);
        Promotion saved = promotionRepository.save(promotion);

        log.info("Promotion created with ID: {}", saved.getId());
        return promotionMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponseDTO getPromotionById(String id) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        return promotionMapper.toResponseDTO(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponseDTO getPromotionByCode(String promoCode) {
        Promotion promotion = promotionRepository.findByPromoCode(promoCode).orElseThrow(() -> new ResourceNotFoundException("Promotion code not found"));
        return promotionMapper.toResponseDTO(promotion);
    }

    @Override
    public PromotionResponseDTO updatePromotion(String id, PromotionRequestDTO requestDTO) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        promotion.setDescription(requestDTO.getDescription());
        promotion.setDiscountValue(requestDTO.getDiscountValue());
        promotion.setMaxDiscountAmount(requestDTO.getMaxDiscountAmount());
        promotion.setMinOrderAmount(requestDTO.getMinOrderAmount());
        promotion.setUsageLimit(requestDTO.getUsageLimit());
        promotion.setStartDate(requestDTO.getStartDate());
        promotion.setEndDate(requestDTO.getEndDate());

        Promotion updated = promotionRepository.save(promotion);
        return promotionMapper.toResponseDTO(updated);
    }

    @Override
    public void deletePromotion(String id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion not found");
        }
        promotionRepository.deleteById(id);
        log.info("Promotion deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream().map(promotionMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getAvailablePromotions() {
        return promotionRepository.findAvailablePromotions(LocalDateTime.now()).stream().map(promotionMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PromotionResponseDTO applyPromotion(String promoCode, BigDecimal orderAmount) {
        Promotion promotion = promotionRepository.findByPromoCode(promoCode).orElseThrow(() -> new ResourceNotFoundException("Promotion code not found"));

        // Validate promotion
        LocalDateTime now = LocalDateTime.now();
        if (!promotion.getIsActive()) {
            throw new InvalidOperationException("Promotion is not active");
        }
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new InvalidOperationException("Promotion is not valid at this time");
        }
        if (promotion.getUsageLimit() != null && promotion.getUsedCount() >= promotion.getUsageLimit()) {
            throw new InvalidOperationException("Promotion usage limit exceeded");
        }
        if (promotion.getMinOrderAmount() != null && orderAmount.compareTo(promotion.getMinOrderAmount()) < 0) {
            throw new InvalidOperationException("Order amount is below minimum required");
        }

        return promotionMapper.toResponseDTO(promotion);
    }

    @Override
    public BigDecimal calculateDiscount(String promoCode, BigDecimal orderAmount) {
        Promotion promotion = promotionRepository.findByPromoCode(promoCode).orElseThrow(() -> new ResourceNotFoundException("Promotion code not found"));

        BigDecimal discount;
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(promotion.getDiscountValue()).divide(new BigDecimal("100"));
        } else {
            discount = promotion.getDiscountValue();
        }

        // Apply max discount limit if exists
        if (promotion.getMaxDiscountAmount() != null && discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }
        return discount;
    }

    @Override
    public void incrementPromotionUsage(String promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        promotion.setUsedCount(promotion.getUsedCount() + 1);
        promotionRepository.save(promotion);

        log.info("Promotion {} usage incremented to {}", promotionId, promotion.getUsedCount());
    }

    @Override
    public void deactivateExpiredPromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findExpiredPromotions(LocalDateTime.now());

        expiredPromotions.forEach(promotion -> {
            promotion.setIsActive(false);
            promotionRepository.save(promotion);
        });

        log.info("Deactivated {} expired promotions", expiredPromotions.size());
    }
}
