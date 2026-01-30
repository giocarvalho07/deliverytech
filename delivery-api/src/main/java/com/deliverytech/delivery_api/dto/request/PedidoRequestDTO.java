package com.deliverytech.delivery_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class PedidoRequestDTO {

    @NotNull(message = "O ID do cliente é obrigatório")
    @Schema(example = "1", description = "ID do cliente que está realizando a compra")
    private Long clienteId;

    @NotNull(message = "O ID do restaurante é obrigatório")
    @Schema(example = "1", description = "ID do restaurante onde o pedido foi feito")
    private Long restauranteId;

    @NotNull(message = "O endereço de entrega é obrigatório")
    @Schema(example = "Rua das Flores, 123", description = "Endereço de entrega específico para este pedido")
    private String enderecoEntrega;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    @Schema(description = "Lista de itens selecionados")
    private List<ItemPedidoRequestDTO> itens;
}
