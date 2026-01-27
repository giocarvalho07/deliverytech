package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.TotalVendasPorRestauranteDTO;
import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints de inteligência de negócio e BI")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    // GET /api/relatorios/vendas-por-restaurante
    @Operation(summary = "Relatório de vendas por restaurante", description = "Buscar um relatório de vendas por restaurante no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de vendas por restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório de vendas por restaurante não encontrado")
    })
    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<ApiSucessResponse<List<TotalVendasPorRestauranteDTO>>> getVendasPorRestaurante() {
        List<TotalVendasPorRestauranteDTO> dados = relatorioService.totalVendasPorRestaurante();

        return ResponseEntity.ok(ApiSucessResponse.<List<TotalVendasPorRestauranteDTO>>builder()
                .sucesso(true)
                .mensagem("Relatório de vendas gerado com sucesso")
                .dados(dados)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/relatorios/produtos-mais-vendidos
    @Operation(summary = "Relatório de produtos mais vendidos", description = "Buscar um relatório de produtos mais vendidosno sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de produtos mais vendidos encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório de produtos mais vendidos não encontrado")
    })
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<ApiSucessResponse<List<?>>> getProdutosMaisVendidos() {
        List<?> dados = relatorioService.produtosMaisVendidos();

        return ResponseEntity.ok(ApiSucessResponse.<List<?>>builder()
                .sucesso(true)
                .mensagem("Ranking de produtos mais vendidos")
                .dados(dados)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/relatorios/clientes-ativos
    @Operation(summary = "Relatório de clientes ativos", description = "Buscar um relatório de clientes ativos no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de clientes ativos encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório de clientes ativos não encontrado")
    })
    @GetMapping("/clientes-ativos")
    public ResponseEntity<ApiSucessResponse<List<?>>> getClientesAtivos() {
        List<?> dados = relatorioService.clientesMaisAtivos();

        return ResponseEntity.ok(ApiSucessResponse.<List<?>>builder()
                .sucesso(true)
                .mensagem("Relatório de clientes mais ativos recuperado")
                .dados(dados)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/relatorios/pedidos-por-periodo
    @Operation(summary = "Relatório de pedidos por período", description = "Buscar um relatório de pedidos por período no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de pedidos por período encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório de pedidos por período não encontrado")
    })
    @GetMapping("/pedidos-por-periodo")
    public ResponseEntity<ApiSucessResponse<List<PedidoResponseDTO>>> getPedidosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        List<PedidoResponseDTO> dados = relatorioService.pedidosPorPeriodo(inicio, fim);

        return ResponseEntity.ok(ApiSucessResponse.<List<PedidoResponseDTO>>builder()
                .sucesso(true)
                .mensagem(String.format("Pedidos encontrados entre %s e %s", inicio, fim))
                .dados(dados)
                .timestamp(LocalDateTime.now())
                .build());
    }
}