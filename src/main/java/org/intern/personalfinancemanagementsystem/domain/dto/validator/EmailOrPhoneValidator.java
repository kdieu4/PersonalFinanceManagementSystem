package org.intern.personalfinancemanagementsystem.domain.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;

public class EmailOrPhoneValidator implements ConstraintValidator<EmailOrPhone, String> {
    @Override
    public void initialize(EmailOrPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.matches(CommonConstant.PHONE_REGEX)) {
            return true;
        } else return value.matches(CommonConstant.EMAIL_REGEX);
    }
}
