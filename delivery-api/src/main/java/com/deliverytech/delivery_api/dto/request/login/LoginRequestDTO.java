package com.deliverytech.delivery_api.dto.request.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class LoginRequestDTO {

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Schema(example = "joao@email.com", description = "E-mail único para login")
        private String email;

        @NotBlank(message = "A senha é obrigatória")
        @Schema(example = "senha@123", description = "Senha única para login")
        private String senha;

    }
