package org.intern.personalfinancemanagementsystem.base;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.http.HttpStatus;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, ChangePasswordRequest> {
    private String first;
    private String second;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(ChangePasswordRequest value, ConstraintValidatorContext context) {
        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(value);
            Object firstObject = beanWrapper.getPropertyValue(first);
            Object secondObject = beanWrapper.getPropertyValue(second);
            return firstObject != null && firstObject.equals(secondObject);
        } catch (BeansException e) {
            return false;
        }
    }
}