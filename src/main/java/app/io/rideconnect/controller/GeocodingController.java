package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/geocoding")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping("/address")
    public ResponseEntity<ApiResponse<String>> getAddressFromCoordinates(
            @Valid @RequestBody LocationDTO location) {
        String address = geocodingService.getAddressFromCoordinates(location);
        return ResponseEntity.ok(ApiResponse.success(address, "Address retrieved successfully"));
    }

    @GetMapping("/coordinates")
    public ResponseEntity<ApiResponse<LocationDTO>> getCoordinatesFromAddress(
            @RequestParam String address) {
        LocationDTO coordinates = geocodingService.getCoordinatesFromAddress(address);
        return ResponseEntity.ok(ApiResponse.success(coordinates, "Coordinates retrieved successfully"));
    }

    @PostMapping("/distance")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> calculateDistance(
            @RequestParam Double fromLat,
            @RequestParam Double fromLon,
            @RequestParam Double toLat,
            @RequestParam Double toLon) {
        LocationDTO from = LocationDTO.builder().latitude(fromLat).longitude(fromLon).build();
        LocationDTO to = LocationDTO.builder().latitude(toLat).longitude(toLon).build();
        java.math.BigDecimal distance = geocodingService.calculateDistance(from, to);
        return ResponseEntity.ok(ApiResponse.success(distance, "Distance calculated successfully"));
    }

    @PostMapping("/duration")
    public ResponseEntity<ApiResponse<Integer>> estimateDuration(
            @RequestParam Double fromLat,
            @RequestParam Double fromLon,
            @RequestParam Double toLat,
            @RequestParam Double toLon) {
        LocationDTO from = LocationDTO.builder().latitude(fromLat).longitude(fromLon).build();
        LocationDTO to = LocationDTO.builder().latitude(toLat).longitude(toLon).build();
        Integer duration = geocodingService.estimateDuration(from, to);
        return ResponseEntity.ok(ApiResponse.success(duration, "Duration estimated successfully"));
    }
}
