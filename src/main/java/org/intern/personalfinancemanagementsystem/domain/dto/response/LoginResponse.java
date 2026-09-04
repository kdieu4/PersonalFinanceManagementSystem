package org.intern.personalfinancemanagementsystem.domain.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

public record LoginResponse(
        String email,
        String refreshToken,
        String accessToken
) {
}
