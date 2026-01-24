package com.deliverytech.delivery_api.dto.response;

import com.deliverytech.delivery_api.enums.StatusPedidos;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class PedidoResumoDTO {
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private StatusPedidos status;
    private BigDecimal valorTotal;
    private String nomeRestaurante;
}
