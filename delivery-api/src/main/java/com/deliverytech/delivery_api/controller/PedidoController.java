package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Fluxo de criação, cancelamento e status de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // POST /api/pedidos - Criar pedido
    @Operation(summary = "Cadastrar novo pedido", description = "Cria um novo pedido no sistema com status ativo por padrão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiSucessResponse<PedidoResponseDTO>> criar(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO novoPedido = pedidoService.criarPedido(dto);

        // Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoPedido.getId())
                .toUri();

        ApiSucessResponse<PedidoResponseDTO> response = ApiSucessResponse.<PedidoResponseDTO>builder()
                .sucesso(true)
                .mensagem("Pedido criado com sucesso")
                .dados(novoPedido)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.created(uri).body(response);
    }

    // GET /api/pedidos/{id} - Buscar pedido completo
    @Operation(summary = "Listar pedido por ID", description = "Lista um novo pedido no sistema com filtros de ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<PedidoResponseDTO>> buscarPorId(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);

        return ResponseEntity.ok(ApiSucessResponse.<PedidoResponseDTO>builder()
                .sucesso(true)
                .mensagem("Pedido localizado")
                .dados(pedido)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/pedidos - Listar com filtros (status, data)
    @Operation(summary = "Listar pedido", description = "Lista um novo pedido no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping
    public ResponseEntity<ApiSucessResponse<List<PedidoResponseDTO>>> listar(
            @RequestParam(required = false) StatusPedidos status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {

        List<PedidoResponseDTO> lista = pedidoService.listarComFiltros(status, data);

        return ResponseEntity.ok(ApiSucessResponse.<List<PedidoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Consulta de pedidos realizada")
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // PATCH /api/pedidos/{id}/status - Atualizar status
    @Operation(summary = "Listar pedido por status", description = "Lista um novo pedido no sistema com filtros de status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiSucessResponse<PedidoResponseDTO>> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedidos status) {
        PedidoResponseDTO atualizado = pedidoService.atualizarStatusPedido(id, status);

        return ResponseEntity.ok(ApiSucessResponse.<PedidoResponseDTO>builder()
                .sucesso(true)
                .mensagem("Status do pedido atualizado para: " + status)
                .dados(atualizado)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // DELETE /api/pedidos/{id} - Cancelar pedido
    @Operation(summary = "Cancelar pedido", description = "Cancelar um pedido no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
    @Operation(summary = "Listar histórico de pedido", description = "Lista histórico de pedido no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico de pedido não encontrado")
    })
    @GetMapping("/clientes/{clienteId}/pedidos")
    public ResponseEntity<ApiSucessResponse<List<PedidoResponseDTO>>> listarPorCliente(@PathVariable Long clienteId) {
        List<PedidoResponseDTO> lista = pedidoService.buscarPedidosPorCliente(clienteId);

        return ResponseEntity.ok(ApiSucessResponse.<List<PedidoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Histórico do cliente recuperado")
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/restaurantes/{restauranteId}/pedidos - Pedidos do restaurante
    @Operation(summary = "Buscar pedido por restaurante", description = "Buscar um pedido por restaurante no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido por restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido por restaurante não encontrado")
    })
    @GetMapping("/restaurantes/{restauranteId}/pedidos")
    public ResponseEntity<ApiSucessResponse<List<PedidoResponseDTO>>> listarPorRestaurante(@PathVariable Long restauranteId) {
        List<PedidoResponseDTO> lista = pedidoService.buscarPedidosPorRestaurante(restauranteId);

        return ResponseEntity.ok(ApiSucessResponse.<List<PedidoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Pedidos do restaurante recuperados")
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // POST /api/pedidos/calcular - calculo total sem salvar (Simulação)
    @Operation(summary = "Calcular total por pedido", description = "Taxa de cálculo total por pedido no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taxa de cálculo total por pedido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Taxa de cálculo total por pedido não encontrado")
    })
    @PostMapping("/calcular")
    public ResponseEntity<ApiSucessResponse<BigDecimal>> calcularTotalSimulado(@RequestBody PedidoRequestDTO dto) {
        BigDecimal total = pedidoService.calcularSimulacao(dto);

        return ResponseEntity.ok(ApiSucessResponse.<BigDecimal>builder()
                .sucesso(true)
                .mensagem("Simulação de cálculo concluída")
                .dados(total)
                .timestamp(LocalDateTime.now())
                .build());
    }
}