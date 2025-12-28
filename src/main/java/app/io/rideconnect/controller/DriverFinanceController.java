package app.io.rideconnect.controller;

import app.io.rideconnect.dto.*;
import app.io.rideconnect.service.DriverFinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DriverFinanceController {

    private final DriverFinanceService driverFinanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverFinanceResponseDTO>> createTransaction(
            @Valid @RequestBody DriverFinanceRequestDTO requestDTO) {
        DriverFinanceResponseDTO transaction = driverFinanceService.createTransaction(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transaction, "Transaction created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverFinanceResponseDTO>> getTransaction(@PathVariable String id) {
        DriverFinanceResponseDTO transaction = driverFinanceService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction retrieved successfully"));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<PageResponseDTO<DriverFinanceResponseDTO>>> getDriverTransactions(
            @PathVariable String driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDTO<DriverFinanceResponseDTO> transactions =
                driverFinanceService.getDriverTransactions(driverId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(transactions, "Transactions retrieved successfully"));
    }

    @GetMapping("/driver/{driverId}/summary")
    public ResponseEntity<ApiResponse<DriverFinanceSummaryDTO>> getDriverFinanceSummary(@PathVariable String driverId) {
        DriverFinanceSummaryDTO summary = driverFinanceService.getDriverFinanceSummary(driverId);
        return ResponseEntity.ok(ApiResponse.success(summary, "Finance summary retrieved successfully"));
    }
}