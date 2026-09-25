package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String name,
        @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
        UUID categoryId,
        @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
        BigDecimal amount,
        @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
        LocalDate startDate,
        @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
        LocalDate endDate
) {
}
