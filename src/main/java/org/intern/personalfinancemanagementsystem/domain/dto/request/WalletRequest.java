package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

import java.math.BigDecimal;

public record WalletRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String name,
        BigDecimal balance,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String currency
) {
}
