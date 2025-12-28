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
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/ride/{rideId}")
    public ResponseEntity<ApiResponse<DeliveryDetailsDTO>> createDelivery(
            @PathVariable String rideId,
            @Valid @RequestBody DeliveryDetailsDTO detailsDTO) {
        DeliveryDetailsDTO delivery = deliveryService.createDelivery(rideId, detailsDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(delivery, "Delivery created successfully"));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<ApiResponse<DeliveryDetailsDTO>> getDeliveryByRideId(@PathVariable String rideId) {
        DeliveryDetailsDTO delivery = deliveryService.getDeliveryByRideId(rideId);
        return ResponseEntity.ok(ApiResponse.success(delivery, "Delivery retrieved successfully"));
    }

    @PatchMapping("/{deliveryId}/proof")
    public ResponseEntity<ApiResponse<DeliveryDetailsDTO>> updateDeliveryProof(
            @PathVariable String deliveryId,
            @Valid @RequestBody DeliveryProofDTO proofDTO) {
        DeliveryDetailsDTO delivery = deliveryService.updateDeliveryProof(deliveryId, proofDTO);
        return ResponseEntity.ok(ApiResponse.success(delivery, "Delivery proof updated successfully"));
    }

    @PatchMapping("/{deliveryId}/complete")
    public ResponseEntity<ApiResponse<DeliveryDetailsDTO>> completeDelivery(@PathVariable String deliveryId) {
        DeliveryDetailsDTO delivery = deliveryService.completeDelivery(deliveryId);
        return ResponseEntity.ok(ApiResponse.success(delivery, "Delivery completed successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<DeliveryDetailsDTO>>> getPendingDeliveries() {
        List<DeliveryDetailsDTO> deliveries = deliveryService.getPendingDeliveries();
        return ResponseEntity.ok(ApiResponse.success(deliveries, "Pending deliveries retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DeliveryDetailsDTO>>> getUserDeliveries(@PathVariable String userId) {
        List<DeliveryDetailsDTO> deliveries = deliveryService.getUserDeliveries(userId);
        return ResponseEntity.ok(ApiResponse.success(deliveries, "User deliveries retrieved successfully"));
    }
}
