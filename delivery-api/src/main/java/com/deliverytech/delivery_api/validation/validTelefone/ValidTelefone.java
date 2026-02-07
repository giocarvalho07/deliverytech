package com.deliverytech.delivery_api.validation.validTelefone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
    String message() default "Telefone inválido. Formato esperado: (XX) 9XXXX-XXXX ou XXXXXXXXXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}