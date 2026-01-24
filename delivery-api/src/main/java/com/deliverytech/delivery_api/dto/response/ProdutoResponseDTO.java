package com.deliverytech.delivery_api.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private Boolean disponivel;
    private String nomeRestaurante; // Apenas o nome para identificação rápida
}