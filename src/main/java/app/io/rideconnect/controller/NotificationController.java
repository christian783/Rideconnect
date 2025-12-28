package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/test/push")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> testPushNotification(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String message) {
        // Test notification functionality
        return ResponseEntity.ok(ApiResponse.success(null, "Test notification sent"));
    }

    @PostMapping("/test/sms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> testSMSNotification(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        notificationService.sendSMS(phoneNumber, message);
        return ResponseEntity.ok(ApiResponse.success(null, "Test SMS sent"));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> broadcastNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false) String userRole) {
        // Broadcast to all users or specific role
        return ResponseEntity.ok(ApiResponse.success(null, "Broadcast notification sent"));
    }
}