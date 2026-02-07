package com.deliverytech.delivery_api.validation.validHorarioFuncionamento;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HorarioFuncionamentoValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHorarioFuncionamento {
    String message() default "Horário inválido. Formato esperado: HH:MM-HH:MM (ex: 08:00-18:00)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
