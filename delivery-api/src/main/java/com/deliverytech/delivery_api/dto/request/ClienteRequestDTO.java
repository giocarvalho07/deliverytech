package com.deliverytech.delivery_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClienteRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Schema(example = "João Silva", description = "Nome completo do cliente")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Schema(example = "joao@email.com", description = "E-mail único para login")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}")
    @Schema(example = "11988887777", description = "Telefone com DDD (apenas números)")
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório")
    @Schema(example = "Rua das Flores, 123, São Paulo - SP", description = "Endereço completo")
    private String endereco;
}