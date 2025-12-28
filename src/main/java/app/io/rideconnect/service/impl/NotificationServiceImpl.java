package app.io.rideconnect.service.impl;

import app.io.rideconnect.config.SmsConfig;
import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SmsConfig smsConfig;
    private final WebClient.Builder webClientBuilder;

    @Override
    @Async
    public void sendRideRequestNotification(String driverId, String rideId) {
        log.info("Sending ride request notification to driver {}", driverId);
        // Implementation would use Firebase Cloud Messaging or SMS
        sendPushNotification(driverId, "New Ride Request",
                "You have a new ride request #" + rideId);
    }

    @Override
    @Async
    public void sendRideAcceptedNotification(String passengerId, String rideId) {
        log.info("Sending ride accepted notification to passenger {}", passengerId);
        sendPushNotification(passengerId, "Ride Accepted",
                "Your ride has been accepted by a driver");
    }

    @Override
    @Async
    public void sendRideStartedNotification(String passengerId, String rideId) {
        log.info("Sending ride started notification to passenger {}", passengerId);
        sendPushNotification(passengerId, "Ride Started",
                "Your ride has started");
    }

    @Override
    @Async
    public void sendRideCompletedNotification(String passengerId, String rideId) {
        log.info("Sending ride completed notification to passenger {}", passengerId);
        sendPushNotification(passengerId, "Ride Completed",
                "Your ride has been completed. Please rate your driver.");
    }

    @Override
    @Async
    public void sendSOSAlert(String rideId, LocationDTO location) {
        log.warn("SOS ALERT for ride {}! Location: {}, {}",
                rideId, location.getLatitude(), location.getLongitude());
        // Send to emergency contacts, admin, nearby authorities
        // This is critical and should have multiple notification channels
    }

    @Override
    @Async
    public void sendPromotionNotification(String userId, String promoCode) {
        log.info("Sending promotion notification to user {}", userId);
        sendPushNotification(userId, "Special Offer!",
                "Use code " + promoCode + " for your next ride");
    }

    @Override
    @Async
    public void sendPaymentNotification(String userId, BigDecimal amount, String status) {
        log.info("Sending payment notification to user {}", userId);
        sendPushNotification(userId, "Payment " + status,
                "Payment of " + amount + " RWF has been " + status.toLowerCase());
    }

    private void sendPushNotification(String userId, String title, String body) {
        // Firebase Cloud Messaging implementation
        log.info("Push notification sent to user {}: {} - {}", userId, title, body);
    }

    @Async
    public void sendSMS(String phoneNumber, String message) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(smsConfig.getApiUrl()).build();

            Map<String, String> smsRequest = new HashMap<>();
            smsRequest.put("to", phoneNumber);
            smsRequest.put("message", message);
            smsRequest.put("from", smsConfig.getSenderId());

            webClient.post()
                    .uri("/send")
                    .header("Authorization", "Bearer " + smsConfig.getApiKey())
                    .bodyValue(smsRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.info("SMS sent successfully to {}", phoneNumber),
                            error -> log.error("Failed to send SMS: {}", error.getMessage())
                    );
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage());
        }
    }
}
