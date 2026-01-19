package com.deliverytech.delivery_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "restaurantes")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String categoria;

    private String endereco;

    private String telefone;

    private BigDecimal avaliação;

    @Column(name = "taxa_entrega")
    private BigDecimal taxaEntrega;

    private Boolean ativo;

    @OneToMany(mappedBy="restaurantes", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();

    @OneToMany(mappedBy="restaurantes", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

}
