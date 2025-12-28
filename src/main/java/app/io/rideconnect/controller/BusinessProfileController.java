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
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    @PostMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponseDTO>> createBusinessProfile(
            @Valid @RequestBody BusinessProfileRequestDTO requestDTO) {
        BusinessProfileResponseDTO business = businessProfileService.createBusinessProfile(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(business, "Business profile created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessProfileResponseDTO>> getBusinessProfile(@PathVariable String id) {
        BusinessProfileResponseDTO business = businessProfileService.getBusinessProfileById(id);
        return ResponseEntity.ok(ApiResponse.success(business, "Business profile retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<BusinessProfileResponseDTO>> getBusinessProfileByUserId(@PathVariable String userId) {
        BusinessProfileResponseDTO business = businessProfileService.getBusinessProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(business, "Business profile retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessProfileResponseDTO>> updateBusinessProfile(
            @PathVariable String id, @Valid @RequestBody BusinessProfileRequestDTO requestDTO) {
        BusinessProfileResponseDTO business = businessProfileService.updateBusinessProfile(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(business, "Business profile updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBusinessProfile(@PathVariable String id) {
        businessProfileService.deleteBusinessProfile(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Business profile deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BusinessProfileResponseDTO>>> searchBusinessByName(
            @RequestParam String name) {
        List<BusinessProfileResponseDTO> businesses = businessProfileService.searchBusinessByName(name);
        return ResponseEntity.ok(ApiResponse.success(businesses, "Businesses retrieved successfully"));
    }
}