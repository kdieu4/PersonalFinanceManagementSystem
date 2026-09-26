package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Budget;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID budgetId,
        String name,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining
) {
    public static BudgetResponse from(Budget budget, BigDecimal spent, BigDecimal remaining) {
        return new BudgetResponse(
                budget.getId(),
                budget.getName(),
                budget.getAmount(),
                spent, remaining);
    }
}
