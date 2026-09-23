package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String purpose,
        @NotNull(message = ErrorMessage.Transaction.NULL_VALUE)
        @Positive(message = ErrorMessage.Transaction.MIN_VALUE)
        BigDecimal amount,
        @NotNull(message = ErrorMessage.Transaction.NULL_VALUE)
        TransactionType type,
        @NotNull(message = ErrorMessage.Transaction.NULL_VALUE)
        UUID walletId,
        @NotNull(message = ErrorMessage.Transaction.NULL_VALUE)
        UUID categoryId
) {
}
