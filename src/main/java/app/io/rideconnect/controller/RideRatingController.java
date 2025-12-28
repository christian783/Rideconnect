package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RideRatingController {

    private final RideRatingService rideRatingService;

    @PostMapping
    public ResponseEntity<ApiResponse<RideRatingResponseDTO>> createRating(
            @Valid @RequestBody RideRatingRequestDTO requestDTO) {
        RideRatingResponseDTO rating = rideRatingService.createRating(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rating, "Rating submitted successfully"));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<ApiResponse<RideRatingResponseDTO>> getRatingByRideId(@PathVariable String rideId) {
        RideRatingResponseDTO rating = rideRatingService.getRatingByRideId(rideId);
        return ResponseEntity.ok(ApiResponse.success(rating, "Rating retrieved successfully"));
    }

    @GetMapping("/driver/{driverId}/average")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getAverageDriverRating(@PathVariable String driverId) {
        java.math.BigDecimal avgRating = rideRatingService.getAverageDriverRating(driverId);
        return ResponseEntity.ok(ApiResponse.success(avgRating, "Average rating retrieved successfully"));
    }

    @GetMapping("/passenger/{passengerId}/average")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getAveragePassengerRating(@PathVariable String passengerId) {
        java.math.BigDecimal avgRating = rideRatingService.getAveragePassengerRating(passengerId);
        return ResponseEntity.ok(ApiResponse.success(avgRating, "Average rating retrieved successfully"));
    }
}
