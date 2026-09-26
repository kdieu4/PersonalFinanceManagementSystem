package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

public record RegisterRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
        @Size(max = CommonConstant.EMAIL_MAX_LENGTH, message = ErrorMessage.INVALID_FORMAT_EMAIL)
        String email,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Pattern(regexp = CommonConstant.PASSWORD_REGEX, message = ErrorMessage.INVALID_FORMAT_PASSWORD)
        String password,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String confirmPassword) {
}
