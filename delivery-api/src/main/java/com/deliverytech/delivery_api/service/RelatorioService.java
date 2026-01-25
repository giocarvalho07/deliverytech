package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.TotalVendasPorRestauranteDTO;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PedidoRepository repository;
    private final ModelMapper modelMapper;

    // 1. Vendas por restaurante
    public List<TotalVendasPorRestauranteDTO> totalVendasPorRestaurante() {
        return repository.obterTotalVendasPorRestaurante();
    }

    // 2. Top produtos (Ex: Top 10 mais vendidos)
    public List<?> produtosMaisVendidos() {
        return repository.obterProdutosMaisVendidos();
    }

    // 3. Clientes mais ativos (Quem comprou mais)
    public List<?> clientesMaisAtivos() {
        return repository.obterClientesMaisAtivos();
    }

    // 4. Pedidos por período
    public List<PedidoResponseDTO> pedidosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByDataPedidoBetween(inicio, fim).stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }
}
