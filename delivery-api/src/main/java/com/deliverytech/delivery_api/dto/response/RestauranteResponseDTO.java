package com.deliverytech.delivery_api.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class RestauranteResponseDTO {
    private Long id;
    private String nome;
    private String endereco;
    private BigDecimal taxaEntrega;
    private Boolean ativo;
}