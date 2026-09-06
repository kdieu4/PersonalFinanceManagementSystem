package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

public record LogoutRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String refreshToken
) {
}
