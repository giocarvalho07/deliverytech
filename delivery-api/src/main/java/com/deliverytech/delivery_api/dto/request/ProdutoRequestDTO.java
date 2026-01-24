package com.deliverytech.delivery_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser no mínimo 0.01")
    private BigDecimal preco;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @NotNull(message = "A disponibilidade deve ser informada")
    private Boolean disponivel;

    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;
}
