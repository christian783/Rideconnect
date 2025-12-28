package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponseDTO>> createPromotion(
            @Valid @RequestBody PromotionRequestDTO requestDTO) {
        PromotionResponseDTO promotion = promotionService.createPromotion(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promotion, "Promotion created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionResponseDTO>> getPromotion(@PathVariable String id) {
        PromotionResponseDTO promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion retrieved successfully"));
    }

    @GetMapping("/code/{promoCode}")
    public ResponseEntity<ApiResponse<PromotionResponseDTO>> getPromotionByCode(@PathVariable String promoCode) {
        PromotionResponseDTO promotion = promotionService.getPromotionByCode(promoCode);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromotionResponseDTO>>> getActivePromotions() {
        List<PromotionResponseDTO> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Active promotions retrieved successfully"));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<PromotionResponseDTO>> applyPromotion(
            @RequestParam String promoCode,
            @RequestParam java.math.BigDecimal orderAmount) {
        PromotionResponseDTO promotion = promotionService.applyPromotion(promoCode, orderAmount);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion applied successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable String id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion deleted successfully"));
    }
}