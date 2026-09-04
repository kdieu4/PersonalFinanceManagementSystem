package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

public record LoginRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
        String email,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String password
) {
}
