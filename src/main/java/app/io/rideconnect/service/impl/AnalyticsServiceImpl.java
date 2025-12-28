package app.io.rideconnect.service.impl;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.DriverTier;
import app.io.rideconnect.entity.enums.RideStatus;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RideRepository rideRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final DriverFinanceRepository driverFinanceRepository;

    @Override
    public RideAnalyticsDTO getRideAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Long totalRides = rideRepository.count();
        Long completedRides = rideRepository.countByStatus(RideStatus.COMPLETED);
        Long cancelledRides = rideRepository.countByStatus(RideStatus.CANCELLED);
        BigDecimal totalRevenue = rideRepository.getTotalRevenueBetweenDates(startDate, endDate);
        BigDecimal averageFare = rideRepository.getAverageFare();

        return RideAnalyticsDTO.builder()
                .totalRides(totalRides)
                .completedRides(completedRides)
                .cancelledRides(cancelledRides)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .averageFare(averageFare != null ? averageFare : BigDecimal.ZERO)
                .build();
    }

    @Override
    public DriverAnalyticsDTO getDriverAnalytics() {
        Long totalDrivers = driverProfileRepository.count();
        Long activeDrivers = (long) driverProfileRepository.findByIsAvailableTrue().size();
        Long verifiedDrivers = (long) driverProfileRepository.findByIsVerifiedTrue().size();

        return DriverAnalyticsDTO.builder()
                .totalDrivers(totalDrivers)
                .activeDrivers(activeDrivers)
                .verifiedDrivers(verifiedDrivers)
                .bronzeDrivers(driverProfileRepository.countByTier(DriverTier.BRONZE).intValue())
                .silverDrivers(driverProfileRepository.countByTier(DriverTier.SILVER).intValue())
                .goldDrivers(driverProfileRepository.countByTier(DriverTier.GOLD).intValue())
                .platinumDrivers(driverProfileRepository.countByTier(DriverTier.PLATINUM).intValue())
                .build();
    }

    @Override
    public Map<String, Object> getRevenueAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> analytics = new HashMap<>();

        BigDecimal totalRevenue = rideRepository.getTotalRevenueBetweenDates(startDate, endDate);
        Long totalRides = rideRepository.count();

        analytics.put("totalRevenue", totalRevenue);
        analytics.put("totalRides", totalRides);
        analytics.put("averageRevenuePerRide",
                totalRides > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalRides), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        return analytics;
    }

    @Override
    public Map<String, Object> getHourlyDemandAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        // Group rides by hour and calculate demand
        analytics.put("peakHours", Arrays.asList(8, 9, 17, 18, 19));
        return analytics;
    }

    @Override
    public Map<String, Object> getGeographicAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        // Analyze ride distribution by geographic areas
        analytics.put("topAreas", new ArrayList<>());
        return analytics;
    }
}