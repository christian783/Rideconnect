package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface AnalyticsService {

    RideAnalyticsDTO getRideAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    DriverAnalyticsDTO getDriverAnalytics();

    Map<String, Object> getRevenueAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getHourlyDemandAnalytics();

    Map<String, Object> getGeographicAnalytics();
}