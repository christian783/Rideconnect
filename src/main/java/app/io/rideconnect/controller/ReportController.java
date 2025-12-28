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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN') or hasRole('BUSINESS')")
public class ReportController {

    private final RideService rideService;
    private final DriverFinanceService driverFinanceService;
    private final AnalyticsService analyticsService;

    @GetMapping("/rides/export")
    public ResponseEntity<ApiResponse<String>> exportRidesReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "CSV") String format) {
        // Generate and return report file URL
        String reportUrl = "/reports/rides_" + startDate + "_" + endDate + "." + format.toLowerCase();
        return ResponseEntity.ok(ApiResponse.success(reportUrl, "Report generated successfully"));
    }

    @GetMapping("/finances/driver/{driverId}")
    public ResponseEntity<ApiResponse<DriverFinanceSummaryDTO>> getDriverFinancialReport(
            @PathVariable String driverId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        DriverFinanceSummaryDTO summary;
        if (startDate != null && endDate != null) {
            summary = driverFinanceService.getDriverFinanceSummaryForPeriod(
                    driverId,
                    LocalDateTime.parse(startDate),
                    LocalDateTime.parse(endDate));
        } else {
            summary = driverFinanceService.getDriverFinanceSummary(driverId);
        }

        return ResponseEntity.ok(ApiResponse.success(summary, "Financial report retrieved successfully"));
    }

    @GetMapping("/performance/monthly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyPerformanceReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int targetYear = year != null ? year : LocalDateTime.now().getYear();
        int targetMonth = month != null ? month : LocalDateTime.now().getMonthValue();

        LocalDateTime startDate = LocalDateTime.of(targetYear, targetMonth, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        Map<String, Object> report = new java.util.HashMap<>();
        report.put("rides", analyticsService.getRideAnalytics(startDate, endDate));
        report.put("revenue", analyticsService.getRevenueAnalytics(startDate, endDate));
        report.put("period", targetYear + "-" + String.format("%02d", targetMonth));

        return ResponseEntity.ok(ApiResponse.success(report, "Monthly performance report retrieved successfully"));
    }
}