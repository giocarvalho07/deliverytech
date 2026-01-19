package com.deliverytech.delivery_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name="itens_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;

    @Column(name = "preco_unitario")
    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="produto_id")
    private Produto produto;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="pedido_id")
    private Pedido pedido;

}