package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        // 1. Dados básicos do pedido
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedidos.PENDENTE);
        pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // 2. Processamento dos Itens e Cálculo de Valor
        BigDecimal somaItens = BigDecimal.ZERO;

        for (var item : pedido.getItens()) {
            // Vincula o item ao pedido (importante para a FK no banco)
            item.setPedido(pedido);

            // Calcula o subtotal do item (quantidade * preço unitário)
            BigDecimal subtotalItem = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            item.setSubtotal(subtotalItem);

            // Soma ao total geral
            somaItens = somaItens.add(subtotalItem);
        }

        // 3. Valor Total = Soma dos itens + Taxa de entrega
        pedido.setValorTotal(somaItens.add(pedido.getTaxaEntrega()));

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> consultarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedidos novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }
}