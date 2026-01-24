package com.deliverytech.delivery_api.dto.request;

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
    private Long clienteId;

    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;

    @NotNull(message = "O endereço de entrega é obrigatório")
    private String enderecoEntrega;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    private List<ItemPedidoRequestDTO> itens;
}
