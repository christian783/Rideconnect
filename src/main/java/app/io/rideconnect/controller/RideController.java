package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<ApiResponse<RideResponseDTO>> createRide(@Valid @RequestBody RideRequestDTO requestDTO) {
        RideResponseDTO ride = rideService.createRide(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ride, "Ride created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideResponseDTO>> getRide(@PathVariable String id) {
        RideResponseDTO ride = rideService.getRideById(id);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride retrieved successfully"));
    }

    @PostMapping("/{rideId}/accept")
    public ResponseEntity<ApiResponse<RideResponseDTO>> acceptRide(
            @PathVariable String rideId, @RequestParam String driverId) {
        RideResponseDTO ride = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride accepted successfully"));
    }

    @PatchMapping("/{rideId}/start")
    public ResponseEntity<ApiResponse<RideResponseDTO>> startRide(@PathVariable String rideId) {
        RideResponseDTO ride = rideService.startRide(rideId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride started successfully"));
    }

    @PatchMapping("/{rideId}/complete")
    public ResponseEntity<ApiResponse<RideResponseDTO>> completeRide(@PathVariable String rideId) {
        RideResponseDTO ride = rideService.completeRide(rideId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride completed successfully"));
    }

    @PatchMapping("/{rideId}/cancel")
    public ResponseEntity<ApiResponse<RideResponseDTO>> cancelRide(
            @PathVariable String rideId, @RequestParam(required = false) String reason) {
        RideResponseDTO ride = rideService.cancelRide(rideId, reason);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride cancelled successfully"));
    }

    @PatchMapping("/{rideId}/status")
    public ResponseEntity<ApiResponse<RideResponseDTO>> updateRideStatus(
            @PathVariable String rideId, @RequestParam String status) {
        RideResponseDTO ride = rideService.updateRideStatus(rideId, status);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride status updated successfully"));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<ApiResponse<PageResponseDTO<RideResponseDTO>>> getPassengerRides(
            @PathVariable String passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDTO<RideResponseDTO> rides = rideService.getPassengerRides(
                passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(rides, "Passenger rides retrieved successfully"));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<PageResponseDTO<RideResponseDTO>>> getDriverRides(
            @PathVariable String driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDTO<RideResponseDTO> rides = rideService.getDriverRides(
                driverId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(rides, "Driver rides retrieved successfully"));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RideResponseDTO>>> getActiveRides() {
        List<RideResponseDTO> rides = rideService.getActiveRides();
        return ResponseEntity.ok(ApiResponse.success(rides, "Active rides retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RideResponseDTO>>> getRidesByStatus(@PathVariable String status) {
        List<RideResponseDTO> rides = rideService.getRidesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(rides, "Rides retrieved successfully"));
    }

    @PostMapping("/calculate-fare")
    public ResponseEntity<ApiResponse<RideResponseDTO>> calculateFare(@Valid @RequestBody RideRequestDTO requestDTO) {
        RideResponseDTO fareEstimate = rideService.calculateFare(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(fareEstimate, "Fare calculated successfully"));
    }

    @PatchMapping("/{rideId}/sos")
    public ResponseEntity<ApiResponse<RideResponseDTO>> activateSOS(
            @PathVariable String rideId, @RequestParam(required = false) String notes) {
        RideResponseDTO ride = rideService.activateSOS(rideId, notes);
        return ResponseEntity.ok(ApiResponse.success(ride, "SOS activated successfully"));
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RideAnalyticsDTO>> getRideAnalytics() {
        RideAnalyticsDTO analytics = rideService.getRideAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Ride analytics retrieved successfully"));
    }
}
