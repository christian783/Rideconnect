package app.io.rideconnect.service;


import app.io.rideconnect.dto.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public interface FareCalculationService {

    BigDecimal calculateBaseFare(String rideType, BigDecimal distance);

    BigDecimal calculateSurgePricing(LocationDTO location, LocalDateTime requestTime);

    BigDecimal calculateTotalFare(String rideType, BigDecimal distance,
                                            LocationDTO location, LocalDateTime requestTime);

    BigDecimal applyPromotion(BigDecimal fare, String promoCode);
}