package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @Size(max = CommonConstant.FULL_NAME_MAX_LENGTH, message = ErrorMessage.INVALID_FORMAT_FULL_NAME)
        String fullName,
        @Pattern(regexp = CommonConstant.PHONE_REGEX, message = ErrorMessage.INVALID_PHONE_NUMBER)
        String phoneNumber,
        @PastOrPresent(message = ErrorMessage.INVALID_DATE_OF_BIRTH)
        LocalDate dateOfBirth
) {
}
