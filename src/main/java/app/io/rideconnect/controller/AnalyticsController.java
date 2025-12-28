package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final RideService rideService;
    private final DriverProfileService driverProfileService;

    @GetMapping("/rides")
    public ResponseEntity<ApiResponse<RideAnalyticsDTO>> getRideAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = startDate != null ?
                LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ?
                LocalDateTime.parse(endDate) : LocalDateTime.now();

        RideAnalyticsDTO analytics = analyticsService.getRideAnalytics(start, end);
        return ResponseEntity.ok(ApiResponse.success(analytics, "Ride analytics retrieved successfully"));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<DriverAnalyticsDTO>> getDriverAnalytics() {
        DriverAnalyticsDTO analytics = analyticsService.getDriverAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Driver analytics retrieved successfully"));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = startDate != null ?
                LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ?
                LocalDateTime.parse(endDate) : LocalDateTime.now();

        Map<String, Object> analytics = analyticsService.getRevenueAnalytics(start, end);
        return ResponseEntity.ok(ApiResponse.success(analytics, "Revenue analytics retrieved successfully"));
    }

    @GetMapping("/demand/hourly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHourlyDemandAnalytics() {
        Map<String, Object> analytics = analyticsService.getHourlyDemandAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Hourly demand analytics retrieved successfully"));
    }

    @GetMapping("/geographic")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGeographicAnalytics() {
        Map<String, Object> analytics = analyticsService.getGeographicAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics, "Geographic analytics retrieved successfully"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary() {
        Map<String, Object> dashboard = new java.util.HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);

        dashboard.put("rides", analyticsService.getRideAnalytics(monthAgo, now));
        dashboard.put("drivers", analyticsService.getDriverAnalytics());
        dashboard.put("revenue", analyticsService.getRevenueAnalytics(monthAgo, now));

        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard summary retrieved successfully"));
    }
}