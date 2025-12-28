package app.io.rideconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverFinanceSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalCommissions;
    private BigDecimal currentBalance;
    private BigDecimal todayEarnings;
    private BigDecimal weekEarnings;
    private BigDecimal monthEarnings;
}