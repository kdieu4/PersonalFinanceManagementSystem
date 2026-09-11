package org.intern.personalfinancemanagementsystem.domain.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailOrPhoneValidator.class)
public @interface EmailOrPhone {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default ErrorMessage.ERROR_EMAIL_OR_PHONE_NUMBER_FORMAT;
}
