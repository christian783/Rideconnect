package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.DriverProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DriverProfileController {

    private final DriverProfileService driverProfileService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> createDriverProfile(
            @Valid @RequestBody DriverProfileRequestDTO requestDTO) {
        DriverProfileResponseDTO driver = driverProfileService.createDriverProfile(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(driver, "Driver profile created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> getDriverProfile(@PathVariable String id) {
        DriverProfileResponseDTO driver = driverProfileService.getDriverProfileById(id);
        return ResponseEntity.ok(ApiResponse.success(driver, "Driver profile retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> getDriverProfileByUserId(@PathVariable String userId) {
        DriverProfileResponseDTO driver = driverProfileService.getDriverProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(driver, "Driver profile retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> updateDriverProfile(
            @PathVariable String id, @Valid @RequestBody DriverProfileRequestDTO requestDTO) {
        DriverProfileResponseDTO driver = driverProfileService.updateDriverProfile(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(driver, "Driver profile updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDriverProfile(@PathVariable String id) {
        driverProfileService.deleteDriverProfile(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Driver profile deleted successfully"));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DriverProfileResponseDTO>>> getAvailableDrivers() {
        List<DriverProfileResponseDTO> drivers = driverProfileService.getAvailableDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers, "Available drivers retrieved successfully"));
    }

    @PostMapping("/nearby")
    public ResponseEntity<ApiResponse<List<DriverProfileResponseDTO>>> getNearbyDrivers(
            @Valid @RequestBody LocationDTO location,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        List<DriverProfileResponseDTO> drivers = driverProfileService.getNearbyDrivers(location, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(drivers, "Nearby drivers retrieved successfully"));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> verifyDriver(@PathVariable String id) {
        DriverProfileResponseDTO driver = driverProfileService.verifyDriver(id);
        return ResponseEntity.ok(ApiResponse.success(driver, "Driver verified successfully"));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> updateAvailability(
            @PathVariable String id, @RequestParam boolean isAvailable) {
        DriverProfileResponseDTO driver = driverProfileService.updateDriverAvailability(id, isAvailable);
        return ResponseEntity.ok(ApiResponse.success(driver, "Availability updated successfully"));
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> updateDriverLocation(
            @PathVariable String id, @Valid @RequestBody LocationDTO location) {
        DriverProfileResponseDTO driver = driverProfileService.updateDriverLocation(id, location);
        return ResponseEntity.ok(ApiResponse.success(driver, "Location updated successfully"));
    }

    @PatchMapping("/{id}/tier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverProfileResponseDTO>> updateDriverTier(
            @PathVariable String id, @RequestParam String tier) {
        DriverProfileResponseDTO driver = driverProfileService.updateDriverTier(id, tier);
        return ResponseEntity.ok(ApiResponse.success(driver, "Driver tier updated successfully"));
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverAnalyticsDTO>> getDriverAnalytics() {
        DriverAnalyticsDTO analytics = driverProfileService.getDriverAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Driver analytics retrieved successfully"));
    }
}