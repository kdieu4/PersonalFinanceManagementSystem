package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.intern.personalfinancemanagementsystem.base.FieldMatch;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

@FieldMatch(first = "newPassword", second = "confirmPassword", message = ErrorMessage.PASSWORD_MISMATCH)
public record ChangePasswordRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String oldPassword,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Pattern(regexp = CommonConstant.PASSWORD_REGEX, message = ErrorMessage.INVALID_FORMAT_PASSWORD)
        String newPassword,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String confirmPassword
) {
}