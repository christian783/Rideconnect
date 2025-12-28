package app.io.rideconnect.service.impl;

import app.io.rideconnect.config.MobileMoneyConfig;
import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.PaymentStatus;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final RideRepository rideRepository;
    private final DriverFinanceService driverFinanceService;
    private final NotificationService notificationService;
    private final MobileMoneyConfig mobileMoneyConfig;
    private final WebClient.Builder webClientBuilder;

    @Override
    public boolean processMobileMoneyPayment(String rideId, String phoneNumber, BigDecimal amount) {
        log.info("Processing mobile money payment for ride {}", rideId);

        try {
            Ride ride = rideRepository.findById(rideId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

            WebClient webClient = webClientBuilder.baseUrl(mobileMoneyConfig.getApiUrl()).build();

            Map<String, Object> paymentRequest = new HashMap<>();
            paymentRequest.put("amount", amount);
            paymentRequest.put("phoneNumber", phoneNumber);
            paymentRequest.put("reference", "RIDE-" + rideId);
            paymentRequest.put("callbackUrl", mobileMoneyConfig.getCallbackUrl());

            // Simulated payment processing
            //TODO: Real implementation would integrate with Mobile Money API (MTN MoMo, Airtel Money, etc.)

            ride.setPaymentStatus(PaymentStatus.COMPLETED);
            rideRepository.save(ride);

            // Process driver payment
            driverFinanceService.processRidePayment(rideId);

            // Send notification
            notificationService.sendPaymentNotification(
                    ride.getPassenger().getId(), amount, "COMPLETED");

            log.info("Mobile money payment processed successfully for ride {}", rideId);
            return true;

        } catch (Exception e) {
            log.error("Mobile money payment failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean processCardPayment(String rideId, String cardToken, BigDecimal amount) {
        log.info("Processing card payment for ride {}", rideId);

        try {
            Ride ride = rideRepository.findById(rideId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

            // Integrate with payment gateway (Stripe, Flutterwave, etc.)
            // Simulated for now

            ride.setPaymentStatus(PaymentStatus.COMPLETED);
            rideRepository.save(ride);

            driverFinanceService.processRidePayment(rideId);

            notificationService.sendPaymentNotification(
                    ride.getPassenger().getId(), amount, "COMPLETED");

            log.info("Card payment processed successfully for ride {}", rideId);
            return true;

        } catch (Exception e) {
            log.error("Card payment failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean processCashPayment(String rideId, BigDecimal amount) {
        log.info("Processing cash payment for ride {}", rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        ride.setPaymentStatus(PaymentStatus.COMPLETED);
        rideRepository.save(ride);

        driverFinanceService.processRidePayment(rideId);

        log.info("Cash payment recorded for ride {}", rideId);
        return true;
    }

    @Override
    public boolean refundPayment(String rideId) {
        log.info("Processing refund for ride {}", rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (ride.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot refund incomplete payment");
        }

        // Process refund through payment gateway
        ride.setPaymentStatus(PaymentStatus.REFUNDED);
        rideRepository.save(ride);

        notificationService.sendPaymentNotification(
                ride.getPassenger().getId(), ride.getActualFare(), "REFUNDED");

        log.info("Refund processed for ride {}", rideId);
        return true;
    }

    @Override
    public String getPaymentStatus(String transactionId) {
        // Query payment gateway for transaction status
        return "COMPLETED";
    }
}