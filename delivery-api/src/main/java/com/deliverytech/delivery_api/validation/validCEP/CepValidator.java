package com.deliverytech.delivery_api.validation.validCEP;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepValidator implements ConstraintValidator<ValidCEP, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        // Regex: 5 dígitos, hífen opcional, 3 dígitos
        return value.matches("\\d{5}-?\\d{3}");
    }
}