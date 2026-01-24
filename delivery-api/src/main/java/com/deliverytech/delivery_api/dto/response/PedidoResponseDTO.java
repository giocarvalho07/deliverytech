package com.deliverytech.delivery_api.dto.response;

import com.deliverytech.delivery_api.enums.StatusPedidos;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class PedidoResponseDTO {
    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private StatusPedidos status;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private String enderecoEntrega;

    // Dados resumidos
    private String nomeCliente;
    private String nomeRestaurante;

    private List<ItemPedidoResponseDTO> itens;
}