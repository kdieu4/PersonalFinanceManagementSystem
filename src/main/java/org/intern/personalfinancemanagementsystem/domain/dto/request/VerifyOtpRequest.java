package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.validator.EmailOrPhone;

public record VerifyOtpRequest(
        @EmailOrPhone
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String identity,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Pattern(regexp = "\\d{6}", message = ErrorMessage.Auth.INVALID_FORMAT_OTP)
        String otp
) {
}
