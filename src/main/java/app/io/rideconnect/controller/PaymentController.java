package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/mobile-money")
    public ResponseEntity<ApiResponse<Boolean>> processMobileMoneyPayment(
            @RequestParam String rideId,
            @RequestParam String phoneNumber,
            @RequestParam java.math.BigDecimal amount) {
        boolean success = paymentService.processMobileMoneyPayment(rideId, phoneNumber, amount);
        return ResponseEntity.ok(ApiResponse.success(success,
                success ? "Payment processed successfully" : "Payment failed"));
    }

    @PostMapping("/card")
    public ResponseEntity<ApiResponse<Boolean>> processCardPayment(
            @RequestParam String rideId,
            @RequestParam String cardToken,
            @RequestParam java.math.BigDecimal amount) {
        boolean success = paymentService.processCardPayment(rideId, cardToken, amount);
        return ResponseEntity.ok(ApiResponse.success(success,
                success ? "Payment processed successfully" : "Payment failed"));
    }

    @PostMapping("/cash")
    public ResponseEntity<ApiResponse<Boolean>> processCashPayment(
            @RequestParam String rideId,
            @RequestParam java.math.BigDecimal amount) {
        boolean success = paymentService.processCashPayment(rideId, amount);
        return ResponseEntity.ok(ApiResponse.success(success, "Cash payment recorded successfully"));
    }

    @PostMapping("/{rideId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> refundPayment(@PathVariable String rideId) {
        boolean success = paymentService.refundPayment(rideId);
        return ResponseEntity.ok(ApiResponse.success(success, "Refund processed successfully"));
    }

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<ApiResponse<String>> getPaymentStatus(@PathVariable String transactionId) {
        String status = paymentService.getPaymentStatus(transactionId);
        return ResponseEntity.ok(ApiResponse.success(status, "Payment status retrieved successfully"));
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> paymentCallback(@RequestBody Map<String, Object> callbackData) {
        // Handle payment gateway callback
        return ResponseEntity.ok().build();
    }
}