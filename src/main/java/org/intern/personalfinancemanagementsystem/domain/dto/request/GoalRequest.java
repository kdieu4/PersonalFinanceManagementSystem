package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String name,

        @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
        @DecimalMin(value = "0.01")
        BigDecimal targetAmount,

        @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
        LocalDate targetDate
) {
}
