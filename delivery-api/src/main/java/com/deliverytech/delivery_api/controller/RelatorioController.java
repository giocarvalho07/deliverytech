package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.TotalVendasPorRestauranteDTO;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio")
public class RelatorioController {

    private final PedidoRepository pedidoRepository;

    public RelatorioController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/total-vendas-por-restaurante")
    public List<TotalVendasPorRestauranteDTO> getTotalVendas() {
        return pedidoRepository.obterTotalVendasPorRestaurante();
    }
}
