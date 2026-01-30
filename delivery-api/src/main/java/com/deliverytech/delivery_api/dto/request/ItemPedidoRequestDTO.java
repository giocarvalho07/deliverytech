package com.deliverytech.delivery_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ItemPedidoRequestDTO {

    @NotNull(message = "O ID do produto é obrigatório")
    @Schema(example = "1", description = "ID do produto")
    private Long produtoId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima é 1")
    @Schema(example = "2", description = "Quantidade desejada")
    private Integer quantidade;
}
