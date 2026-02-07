package com.deliverytech.delivery_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    @Schema(example = "Pizza Margherita", description = "Nome do item do cardápio")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 10, message = "A descrição deve ter no mínimo 10 caracteres")
    @Schema(example = "Molho de tomate, muçarela e manjericão fresco", description = "Detalhes dos ingredientes")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @DecimalMax(value = "500.00", message = "O preço máximo permitido é R$ 500,00")
    @Schema(example = "45.90", description = "Preço unitário do produto")
    private BigDecimal preco;

    @NotBlank(message = "A categoria é obrigatória")
    @Schema(example = "Pizzas", description = "Categoria interna do cardápio")
    private String categoria;

    @NotNull(message = "A disponibilidade deve ser informada")
    @Schema(example = "true", description = "Disponibilidade imediata no estoque/cozinha")
    private Boolean disponivel;

    @NotNull(message = "O ID do restaurante é obrigatório")
    @Schema(example = "1", description = "ID do restaurante proprietário deste produto")
    private Long restauranteId;
}
