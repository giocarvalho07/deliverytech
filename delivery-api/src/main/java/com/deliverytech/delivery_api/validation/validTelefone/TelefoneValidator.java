package com.deliverytech.delivery_api.validation.validTelefone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        // Remove parênteses, espaços e hífens para validar apenas os números
        String numeros = value.replaceAll("\\D", "");

        // Valida se tem 10 (fixo) ou 11 (celular) dígitos
        return numeros.length() >= 10 && numeros.length() <= 11;
    }
}