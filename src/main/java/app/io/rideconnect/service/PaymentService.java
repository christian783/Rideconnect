package app.io.rideconnect.service;


public interface PaymentService {

    boolean processMobileMoneyPayment(String rideId, String phoneNumber, java.math.BigDecimal amount);

    boolean processCardPayment(String rideId, String cardToken, java.math.BigDecimal amount);

    boolean processCashPayment(String rideId, java.math.BigDecimal amount);

    boolean refundPayment(String rideId);

    String getPaymentStatus(String transactionId);
}