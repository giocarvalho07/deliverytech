package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/restaurantes")
@Tag(name = "Restaurantes", description = "Gerenciamento de estabelecimentos parceiros")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // POST /api/restaurantes - Cadastrar restaurante
    @Operation(summary = "Cadastrar novo restaurante", description = "Cria um novo restaurante no sistema com status ativo por padrão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiSucessResponse<RestauranteResponseDTO>> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO novoRestaurante = restauranteService.cadastrarRestaurante(dto);
        // Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoRestaurante.getId())
                .toUri();

        ApiSucessResponse<RestauranteResponseDTO> response = ApiSucessResponse.<RestauranteResponseDTO>builder()
                .sucesso(true)
                .mensagem("Restaurante cadastrado com sucesso")
                .dados(novoRestaurante)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.created(uri).body(response);
    }

    // GET /api/restaurantes - Listar com filtros opcionais (categoria, ativo)
    @Operation(summary = "Listar restaurantes", description = "Lista um novo restaurante no sistema com filtros opcionais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping
    public ResponseEntity<ApiSucessResponse<List<RestauranteResponseDTO>>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo) {

        List<RestauranteResponseDTO> lista = restauranteService.listarComFiltros(categoria, ativo);

        return ResponseEntity.ok(ApiSucessResponse.<List<RestauranteResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Busca realizada com sucesso")
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @Operation(summary = "Listar restaurantes por ID", description = "Lista um novo restaurante no sistema com filtros de ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<RestauranteResponseDTO>> buscarPorId(@PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarRestaurantePorId(id);

        return ResponseEntity.ok(ApiSucessResponse.<RestauranteResponseDTO>builder()
                .sucesso(true)
                .mensagem("Restaurante localizado")
                .dados(restaurante)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante completo
    @Operation(summary = "Atualizar restaurantes por ID", description = "Atualiza um restaurante no sistema com novas informações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<RestauranteResponseDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO atualizado = restauranteService.atualizarRestaurante(id, dto);

        return ResponseEntity.ok(ApiSucessResponse.<RestauranteResponseDTO>builder()
                .sucesso(true)
                .mensagem("Restaurante atualizado com sucesso")
                .dados(atualizado)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // PATCH /api/restaurantes/{id}/status - Ativar/desativar (Alteração parcial)
    @Operation(summary = "Inativar restaurantes", description = "Inativar um restaurante no sistema com alterações parciais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        restauranteService.atualizarStatus(id, ativo);
        // Operações de status/toggle usam 204 No Content
        return ResponseEntity.noContent().build();
    }

    // GET /api/restaurantes/categoria/{categoria} - Busca direta por categoria na URL
    @Operation(summary = "Buscar restaurantes por categoria", description = "Buscar um restaurante no sistema por categorias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiSucessResponse<List<RestauranteResponseDTO>>> buscarPorCategoria(@PathVariable String categoria) {
        List<RestauranteResponseDTO> lista = restauranteService.buscarRestaurantesPorCategoria(categoria);

        return ResponseEntity.ok(ApiSucessResponse.<List<RestauranteResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Resultados para a categoria: " + categoria)
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa dinâmica
    @Operation(summary = "Calcular taxa dinâmica por restaurantes", description = "Taxa de cálculo por restaurante no sistema de forma dinâmica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taxa cálculada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<ApiSucessResponse<BigDecimal>> calcularTaxa(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);

        return ResponseEntity.ok(ApiSucessResponse.<BigDecimal>builder()
                .sucesso(true)
                .mensagem("Taxa calculada")
                .dados(taxa)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/restaurantes/proximos/{cep} - Buscar por proximidade de CEP
    @Operation(summary = "Buscar restaurantes por CEP", description = "Buscar um restaurante no sistema por CEP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante/CEP encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante/CEP não encontrado")
    })
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<ApiSucessResponse<List<RestauranteResponseDTO>>> buscarProximos(@PathVariable String cep) {
        List<RestauranteResponseDTO> lista = restauranteService.buscarRestaurantesProximos(cep);

        return ResponseEntity.ok(ApiSucessResponse.<List<RestauranteResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Restaurantes próximos ao CEP: " + cep)
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }
}