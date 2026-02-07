package com.deliverytech.delivery_api.validation.validHorarioFuncionamento;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        // Regex para validar:
        // HH:MM entre 00:00 e 23:59
        // Separado por hífen (-)
        // Outro HH:MM entre 00:00 e 23:59
        String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$";

        return value.matches(regex);
    }
}