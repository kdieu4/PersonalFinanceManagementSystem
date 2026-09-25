package org.intern.personalfinancemanagementsystem.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ReconciliationResponse(
        UUID walletId,
        String walletName,
        LocalDate startDate,
        LocalDate endDate,

        BigDecimal openingBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal expectedBalance,
        BigDecimal actualBalance,
        BigDecimal difference,

        long transactionCount
) {
}
