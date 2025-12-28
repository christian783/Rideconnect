package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.RideTracking;
import app.io.rideconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RideTrackingController {

    private final RideTrackingService rideTrackingService;

    @PostMapping("/ride/{rideId}")
    public ResponseEntity<ApiResponse<Void>> addTrackingPoint(
            @PathVariable String rideId,
            @Valid @RequestBody LocationDTO location,
            @RequestParam(required = false) Double speed,
            @RequestParam(required = false) Double heading) {
        rideTrackingService.addTrackingPoint(rideId, location, speed, heading);
        return ResponseEntity.ok(ApiResponse.success(null, "Tracking point added successfully"));
    }

    @GetMapping("/ride/{rideId}/history")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getRideTrackingHistory(@PathVariable String rideId) {
        List<LocationDTO> history = rideTrackingService.getRideTrackingHistory(rideId);
        return ResponseEntity.ok(ApiResponse.success(history, "Tracking history retrieved successfully"));
    }

    @GetMapping("/ride/{rideId}/latest")
    public ResponseEntity<ApiResponse<List<RideTracking>>> getLatestTrackingPoint(@PathVariable String rideId) {
        List<RideTracking> ride_trackings= rideTrackingService.getLatestTrackingPoint(rideId);
        return ResponseEntity.ok(ApiResponse.success(ride_trackings, "Latest tracking point retrieved successfully"));
    }
}