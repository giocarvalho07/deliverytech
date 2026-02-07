package com.deliverytech.delivery_api.dto.request.login;

import com.deliverytech.delivery_api.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Schema(example = "João Silva", description = "Nome completo do cliente")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Schema(example = "joao.silva@email.com", description = "Email completo do cliente")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Schema(example = "senha@123", description = "Senha completa do cliente")
    private String senha;

    @NotNull(message = "A Role é obrigatória")
    @Schema(example = "ADMIN", description = "ROLE do cliente")
    private UserRole role;
}