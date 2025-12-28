package app.io.rideconnect.controller;

import app.io.rideconnect.dto.ApiResponse;
import app.io.rideconnect.dto.RideRequestDTO;
import app.io.rideconnect.service.FareCalculationService;
import app.io.rideconnect.service.GeocodingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/fare")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FareController {

    private final FareCalculationService fareCalculationService;
    private final GeocodingService geocodingService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateFare(
            @Valid @RequestBody RideRequestDTO requestDTO) {

        java.math.BigDecimal distance = geocodingService.calculateDistance(
                requestDTO.getPickupLocation(), requestDTO.getDropoffLocation());

        java.math.BigDecimal baseFare = fareCalculationService.calculateBaseFare(
                requestDTO.getRideType(), distance);

        java.math.BigDecimal surgePricing = fareCalculationService.calculateSurgePricing(
                requestDTO.getPickupLocation(), LocalDateTime.now());

        java.math.BigDecimal totalFare = fareCalculationService.calculateTotalFare(
                requestDTO.getRideType(), distance,
                requestDTO.getPickupLocation(), LocalDateTime.now());

        Integer duration = geocodingService.estimateDuration(
                requestDTO.getPickupLocation(), requestDTO.getDropoffLocation());

        Map<String, Object> fareDetails = new java.util.HashMap<>();
        fareDetails.put("distance", distance);
        fareDetails.put("baseFare", baseFare);
        fareDetails.put("surgeMultiplier", surgePricing);
        fareDetails.put("totalFare", totalFare);
        fareDetails.put("estimatedDuration", duration);
        fareDetails.put("currency", "RWF");

        return ResponseEntity.ok(ApiResponse.success(fareDetails, "Fare calculated successfully"));
    }

    @GetMapping("/estimate")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> estimateFare(
            @RequestParam String rideType,
            @RequestParam Double distance) {
        java.math.BigDecimal fare = fareCalculationService.calculateBaseFare(
                rideType, java.math.BigDecimal.valueOf(distance));
        return ResponseEntity.ok(ApiResponse.success(fare, "Fare estimated successfully"));
    }
}