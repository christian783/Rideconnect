package app.io.rideconnect.service.impl;

import app.io.rideconnect.config.AppConfig;
import app.io.rideconnect.dto.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FareCalculationServiceImpl implements FareCalculationService {

    private final AppConfig appConfig;

    @Override
    public BigDecimal calculateBaseFare(String rideType, BigDecimal distance) {
        BigDecimal baseFare;
        BigDecimal perKmRate;

        switch (rideType.toUpperCase()) {
            case "PASSENGER":
                baseFare = appConfig.getBaseFare().getCar();
                perKmRate = appConfig.getPerKmRate().getCar();
                break;
            case "GOODS_DELIVERY":
                baseFare = appConfig.getBaseFare().getMotorcycle();
                perKmRate = appConfig.getPerKmRate().getMotorcycle();
                break;
            case "CARPOOL":
                baseFare = appConfig.getBaseFare().getCar().multiply(new BigDecimal("0.8"));
                perKmRate = appConfig.getPerKmRate().getCar().multiply(new BigDecimal("0.8"));
                break;
            default:
                baseFare = appConfig.getBaseFare().getCar();
                perKmRate = appConfig.getPerKmRate().getCar();
        }

        return baseFare.add(perKmRate.multiply(distance));
    }

    @Override
    public BigDecimal calculateSurgePricing(LocationDTO location, LocalDateTime requestTime) {
        // Check if location is in a hotspot
        // Check time of day (peak hours)
        // Return surge multiplier

        int hour = requestTime.getHour();
        boolean isPeakHour = (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19);

        if (isPeakHour) {
            return appConfig.getSurgeMultiplier();
        }

        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal calculateTotalFare(String rideType, BigDecimal distance,
                                         LocationDTO location, LocalDateTime requestTime) {
        BigDecimal baseFare = calculateBaseFare(rideType, distance);
        BigDecimal surgeMultiplier = calculateSurgePricing(location, requestTime);

        BigDecimal totalFare = baseFare.multiply(surgeMultiplier);

        log.info("Calculated fare: Base={}, Surge={}, Total={}",
                baseFare, surgeMultiplier, totalFare);

        return totalFare.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal applyPromotion(BigDecimal fare, String promoCode) {
        // This would integrate with PromotionService
        // For now, returning original fare
        return fare;
    }
}