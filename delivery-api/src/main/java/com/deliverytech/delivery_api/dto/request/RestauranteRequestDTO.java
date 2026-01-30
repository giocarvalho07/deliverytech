package com.deliverytech.delivery_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "Pizzaria Bella Napoli", description = "Nome fantasia do restaurante")
    private String nome;

    @NotBlank(message = "O endereço é obrigatório")
    @Schema(example = "Italiana", description = "Categoria culinária")
    private String endereco;

    @NotNull(message = "A taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "A taxa de entrega não pode ser negativa")
    @Schema(example = "7.50", description = "Taxa fixa de entrega")
    private BigDecimal taxaEntrega;

    @NotNull(message = "O status de atividade deve ser informado")
    @Schema(example = "true", description = "Define se o restaurante aparece na listagem pública")
    private Boolean ativo;
}