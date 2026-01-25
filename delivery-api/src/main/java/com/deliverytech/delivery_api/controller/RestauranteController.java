package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.response.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
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
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO novoRestaurante = restauranteService.cadastrarRestaurante(dto);
        return new ResponseEntity<>(novoRestaurante, HttpStatus.CREATED);
    }

    // GET /api/restaurantes - Listar com filtros opcionais (categoria, ativo)
    @Operation(summary = "Listar restaurantes", description = "Lista um novo restaurante no sistema com filtros opcionais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(restauranteService.listarComFiltros(categoria, ativo));
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @Operation(summary = "Listar restaurantes por ID", description = "Lista um novo restaurante no sistema com filtros de ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantePorId(id));
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante completo
    @Operation(summary = "Atualizar restaurantes por ID", description = "Atualiza um restaurante no sistema com novas informações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody RestauranteRequestDTO dto) {
        return ResponseEntity.ok(restauranteService.atualizarRestaurante(id, dto));
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
        return ResponseEntity.noContent().build();
    }

    // GET /api/restaurantes/categoria/{categoria} - Busca direta por categoria na URL
    @Operation(summary = "Buscar restaurantes por categoria", description = "Buscar um restaurante no sistema por categorias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesPorCategoria(categoria));
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa dinâmica
    @Operation(summary = "Calcular taxa dinâmica por restaurantes", description = "Taxa de cálculo por restaurante no sistema de forma dinâmica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taxa cálculada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxa(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }

    // GET /api/restaurantes/proximos/{cep} - Buscar por proximidade de CEP
    @Operation(summary = "Buscar restaurantes por CEP", description = "Buscar um restaurante no sistema por CEP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante/CEP encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante/CEP não encontrado")
    })
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(@PathVariable String cep) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesProximos(cep));
    }
}