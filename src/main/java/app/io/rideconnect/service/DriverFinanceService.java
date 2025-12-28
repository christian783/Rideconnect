package app.io.rideconnect.service;

import app.io.rideconnect.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface DriverFinanceService {

    DriverFinanceResponseDTO createTransaction(DriverFinanceRequestDTO requestDTO);

    List<DriverFinanceResponseDTO> getDriverTransactionsBetweenDates(String driverId, LocalDateTime startDate, LocalDateTime endDate);

    DriverFinanceSummaryDTO getDriverFinanceSummary(String driverId);

    DriverFinanceSummaryDTO getDriverFinanceSummaryForPeriod(String driverId, LocalDateTime startDate, LocalDateTime endDate);

    void processRidePayment(String rideId);

    DriverFinanceResponseDTO getTransactionById(String id);

    PageResponseDTO<DriverFinanceResponseDTO> getDriverTransactions(String driverId, Pageable pageable);
}
