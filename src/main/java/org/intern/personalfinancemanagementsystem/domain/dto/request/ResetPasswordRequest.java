package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.validator.EmailOrPhone;
import org.intern.personalfinancemanagementsystem.domain.dto.validator.FieldMatch;

@FieldMatch(first = "newPassword", second = "confirmPassword", message = ErrorMessage.PASSWORD_MISMATCH)
public record ResetPasswordRequest(
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @EmailOrPhone
        String identifier,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        @Pattern(regexp = CommonConstant.PASSWORD_REGEX, message = ErrorMessage.INVALID_FORMAT_PASSWORD)
        String newPassword,

        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String confirmPassword
) {
}
