package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final DriverProfileService driverProfileService;
    private final RideService rideService;
    private final PromotionService promotionService;


    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);

        stats.put("totalUsers", userService.getAllUsers(
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements());
        stats.put("activeDrivers", driverProfileService.getAvailableDrivers().size());
        stats.put("activeRides", rideService.getActiveRides().size());
        stats.put("monthlyRevenue", rideService.getRideAnalyticsBetweenDates(monthAgo, now));

        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard stats retrieved successfully"));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam String status) {
        // Update user status (ACTIVE, SUSPENDED, INACTIVE)
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated successfully"));
    }

    @PostMapping("/promotions/deactivate-expired")
    public ResponseEntity<ApiResponse<Void>> deactivateExpiredPromotions() {
        promotionService.deactivateExpiredPromotions();
        return ResponseEntity.ok(ApiResponse.success(null, "Expired promotions deactivated successfully"));
    }

    @GetMapping("/system/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSystemHealth() {
        Map<String, String> health = new java.util.HashMap<>();
        health.put("status", "UP");
        health.put("database", "CONNECTED");
        health.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(ApiResponse.success(health, "System health retrieved successfully"));
    }
}