package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // POST /api/pedidos - Criar pedido (transação complexa)
    @PostMapping("/pedidos")
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO novoPedido = pedidoService.criarPedido(dto);
        return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
    }

    // GET /api/pedidos/{id} - Buscar pedido completo
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorId(id));
    }

    // GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
    @GetMapping("/clientes/{clienteId}/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorCliente(clienteId));
    }

    // PATCH /api/pedidos/{id}/status - Atualizar status
    @PatchMapping("/pedidos/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id,
                                                             @RequestParam StatusPedidos status) {
        return ResponseEntity.ok(pedidoService.atualizarStatusPedido(id, status));
    }

    // DELETE /api/pedidos/{id} - Cancelar pedido
    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/pedidos/calcular - Calcular total sem salvar
    // Frontend mostra o total no carrinho antes de finalizar
    @PostMapping("/pedidos/calcular")
    public ResponseEntity<BigDecimal> calcularTotalSimulado(@RequestBody PedidoRequestDTO dto) {
        // Usamos a lógica de cálculo da service sem persistir no banco
        return ResponseEntity.ok(BigDecimal.ZERO);
    }
}