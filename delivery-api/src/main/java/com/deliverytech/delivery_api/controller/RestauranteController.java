package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.response.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // POST /api/restaurantes - Cadastrar restaurante
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO novoRestaurante = restauranteService.cadastrarRestaurante(dto);
        return new ResponseEntity<>(novoRestaurante, HttpStatus.CREATED);
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantePorId(id));
    }

    // GET /api/restaurantes - Listar disponíveis (Ativos)
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listarDisponiveis() {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesDisponiveis());
    }

    // GET /api/restaurantes/categoria/{categoria} - Por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesPorCategoria(categoria));
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody RestauranteRequestDTO dto) {
        return ResponseEntity.ok(restauranteService.atualizarRestaurante(id, dto));
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxa(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }
}