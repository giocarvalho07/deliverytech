package com.deliverytech.delivery_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class RestauranteRequestDTO {

    @NotBlank(message = "O nome do restaurante é obrigatório")
    private String nome;

    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

    @NotNull(message = "A taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "A taxa de entrega não pode ser negativa")
    private BigDecimal taxaEntrega;

    @NotNull(message = "O status de atividade deve ser informado")
    private Boolean ativo;
}