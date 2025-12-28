package app.io.rideconnect.service.impl;


import app.io.rideconnect.dto.*;
import app.io.rideconnect.entity.*;
import app.io.rideconnect.entity.enums.TransactionType;
import app.io.rideconnect.exception.*;
import app.io.rideconnect.mapper.*;
import app.io.rideconnect.repository.*;
import app.io.rideconnect.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverFinanceServiceImpl implements DriverFinanceService {

    private final DriverFinanceRepository driverFinanceRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final RideRepository rideRepository;
    private final DriverFinanceMapper driverFinanceMapper;

    @Override
    public DriverFinanceResponseDTO createTransaction(DriverFinanceRequestDTO requestDTO) {
        log.info("Creating finance transaction for driver ID: {}", requestDTO.getDriverId());

        DriverProfile driver = driverProfileRepository.findById(requestDTO.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        // Get current balance
        BigDecimal currentBalance = driverFinanceRepository.findLatestTransaction(requestDTO.getDriverId())
                .map(DriverFinance::getBalance)
                .orElse(BigDecimal.ZERO);

        // Calculate new balance
        TransactionType type = TransactionType.valueOf(requestDTO.getTransactionType().toUpperCase());
        BigDecimal amount = requestDTO.getAmount();
        BigDecimal newBalance = currentBalance;

        if (type == TransactionType.INCOME || type == TransactionType.BONUS || type == TransactionType.REFERRAL) {
            newBalance = newBalance.add(amount);
        } else {
            newBalance = newBalance.subtract(amount);
        }

        DriverFinance transaction = driverFinanceMapper.toEntity(requestDTO);
        transaction.setDriver(driver);
        transaction.setBalance(newBalance);

        if (requestDTO.getRideId() != null) {
            Ride ride = rideRepository.findById(requestDTO.getRideId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
            transaction.setRide(ride);
        }

        DriverFinance saved = driverFinanceRepository.save(transaction);
        log.info("Finance transaction created with ID: {}", saved.getId());

        return driverFinanceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverFinanceResponseDTO getTransactionById(String id) {
        DriverFinance transaction = driverFinanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return driverFinanceMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DriverFinanceResponseDTO> getDriverTransactions(String driverId, Pageable pageable) {
        Page<DriverFinance> transactionPage = driverFinanceRepository.findByDriverId(driverId, pageable);

        List<DriverFinanceResponseDTO> content = transactionPage.getContent().stream()
                .map(driverFinanceMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<DriverFinanceResponseDTO>builder()
                .content(content)
                .pageNumber(transactionPage.getNumber())
                .pageSize(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .last(transactionPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverFinanceResponseDTO> getDriverTransactionsBetweenDates(
            String driverId, LocalDateTime startDate, LocalDateTime endDate) {
        return driverFinanceRepository.findByDriverIdAndDateBetween(driverId, startDate, endDate).stream()
                .map(driverFinanceMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DriverFinanceSummaryDTO getDriverFinanceSummary(String driverId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.minusMonths(1);

        BigDecimal totalIncome = driverFinanceRepository
                .sumByDriverIdAndType(driverId, TransactionType.INCOME);
        BigDecimal totalExpenses = driverFinanceRepository
                .sumByDriverIdAndType(driverId, TransactionType.EXPENSE);
        BigDecimal totalCommissions = driverFinanceRepository
                .sumByDriverIdAndType(driverId, TransactionType.COMMISSION);

        BigDecimal currentBalance = driverFinanceRepository.findLatestTransaction(driverId)
                .map(DriverFinance::getBalance)
                .orElse(BigDecimal.ZERO);

        BigDecimal todayEarnings = driverFinanceRepository
                .sumByDriverIdAndTypeAndDateBetween(driverId, TransactionType.INCOME, startOfDay, now);
        BigDecimal weekEarnings = driverFinanceRepository
                .sumByDriverIdAndTypeAndDateBetween(driverId, TransactionType.INCOME, startOfWeek, now);
        BigDecimal monthEarnings = driverFinanceRepository
                .sumByDriverIdAndTypeAndDateBetween(driverId, TransactionType.INCOME, startOfMonth, now);

        return DriverFinanceSummaryDTO.builder()
                .totalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO)
                .totalExpenses(totalExpenses != null ? totalExpenses : BigDecimal.ZERO)
                .totalCommissions(totalCommissions != null ? totalCommissions : BigDecimal.ZERO)
                .currentBalance(currentBalance)
                .todayEarnings(todayEarnings != null ? todayEarnings : BigDecimal.ZERO)
                .weekEarnings(weekEarnings != null ? weekEarnings : BigDecimal.ZERO)
                .monthEarnings(monthEarnings != null ? monthEarnings : BigDecimal.ZERO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverFinanceSummaryDTO getDriverFinanceSummaryForPeriod(
            String driverId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal income = driverFinanceRepository
                .sumByDriverIdAndTypeAndDateBetween(driverId, TransactionType.INCOME, startDate, endDate);
        BigDecimal expenses = driverFinanceRepository
                .sumByDriverIdAndTypeAndDateBetween(driverId, TransactionType.EXPENSE, startDate, endDate);
        BigDecimal commissions = driverFinanceRepository
                .sumByDriverIdAndTypeAndDateBetween(driverId, TransactionType.COMMISSION, startDate, endDate);

        return DriverFinanceSummaryDTO.builder()
                .totalIncome(income != null ? income : BigDecimal.ZERO)
                .totalExpenses(expenses != null ? expenses : BigDecimal.ZERO)
                .totalCommissions(commissions != null ? commissions : BigDecimal.ZERO)
                .build();
    }

    @Override
    public void processRidePayment(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (ride.getDriver() == null) {
            throw new InvalidOperationException("Ride has no assigned driver");
        }

        DriverProfile driver = ride.getDriver().getDriverProfile();
        if (driver == null) {
            throw new InvalidOperationException("Driver profile not found");
        }

        BigDecimal fare = ride.getActualFare();
        BigDecimal commissionRate = new BigDecimal("0.15"); // 15% commission
        BigDecimal commission = fare.multiply(commissionRate);
        BigDecimal driverEarning = fare.subtract(commission);

        // Create income transaction
        DriverFinanceRequestDTO incomeDTO = DriverFinanceRequestDTO.builder()
                .driverId(driver.getId())
                .transactionType("INCOME")
                .amount(driverEarning)
                .description("Ride fare - Ride #" + rideId)
                .category("RIDE_FARE")
                .rideId(rideId)
                .build();
        createTransaction(incomeDTO);

        // Create commission transaction
        DriverFinanceRequestDTO commissionDTO = DriverFinanceRequestDTO.builder()
                .driverId(driver.getId())
                .transactionType("COMMISSION")
                .amount(commission)
                .description("Platform commission - Ride #" + rideId)
                .category("COMMISSION")
                .rideId(rideId)
                .build();
        createTransaction(commissionDTO);

        log.info("Ride payment processed for ride {}: Driver earning = {}, Commission = {}",
                rideId, driverEarning, commission);
    }
}