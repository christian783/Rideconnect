package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotspots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotspotController {

    private final HotspotService hotspotService;

    @PostMapping("/nearby")
    public ResponseEntity<ApiResponse<List<HotspotResponseDTO>>> getNearbyHotspots(
            @Valid @RequestBody LocationDTO location,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        List<HotspotResponseDTO> hotspots = hotspotService.getNearbyHotspots(location, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(hotspots, "Nearby hotspots retrieved successfully"));
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<HotspotResponseDTO>>> getTopHotspots(
            @RequestParam(defaultValue = "10") int limit) {
        List<HotspotResponseDTO> hotspots = hotspotService.getTopHotspots(limit);
        return ResponseEntity.ok(ApiResponse.success(hotspots, "Top hotspots retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotspotResponseDTO>> getHotspot(@PathVariable String id) {
        HotspotResponseDTO hotspot = hotspotService.getHotspotById(id);
        return ResponseEntity.ok(ApiResponse.success(hotspot, "Hotspot retrieved successfully"));
    }
}