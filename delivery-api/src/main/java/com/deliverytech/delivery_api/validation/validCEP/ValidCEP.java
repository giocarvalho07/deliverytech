package com.deliverytech.delivery_api.validation.validCEP;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CepValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {

    // Mensagem padrão caso a validação falhe
    String message() default "CEP inválido. Formato esperado: 00000-000 ou 00000000";

    // Padrões do Bean Validation (obrigatórios)
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}