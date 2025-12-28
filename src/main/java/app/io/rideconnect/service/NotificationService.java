package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;

public interface NotificationService {

    void sendRideRequestNotification(String driverId, String rideId);

    void sendRideAcceptedNotification(String passengerId, String rideId);

    void sendRideStartedNotification(String passengerId, String rideId);

    void sendRideCompletedNotification(String passengerId, String rideId);

    void sendSOSAlert(String rideId, LocationDTO location);

    void sendPromotionNotification(String userId, String promoCode);

    void sendSMS(String phoneNumber, String message);

    void sendPaymentNotification(String userId, java.math.BigDecimal amount, String status);
}