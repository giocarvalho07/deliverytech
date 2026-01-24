package com.deliverytech.delivery_api.dto;

import java.math.BigDecimal;

public class TotalVendasPorRestauranteDTO {
    private String restaurante;
    private BigDecimal totalVendas;

    public TotalVendasPorRestauranteDTO(String restaurante, BigDecimal totalVendas) {
        this.restaurante = restaurante;
        this.totalVendas = totalVendas;
    }

    public String getRestaurante(){
        return restaurante;
    }

    public BigDecimal getTotalVendas(){
        return totalVendas;
    }
}
