package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.ProdutoResponseDTO;
import com.deliverytech.delivery_api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Gestão do cardápio dos restaurantes")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // POST /api/produtos
    @Operation(summary = "Cadastrar novo produto", description = "Cria um novo produto no sistema com status ativo por padrão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiSucessResponse<ProdutoResponseDTO>> cadastrar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO novoProduto = produtoService.cadastrarProduto(dto);
        // Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoProduto.getId())
                .toUri();

        ApiSucessResponse<ProdutoResponseDTO> response = ApiSucessResponse.<ProdutoResponseDTO>builder()
                .sucesso(true)
                .mensagem("Produto cadastrado com sucesso")
                .dados(novoProduto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.created(uri).body(response);
    }

    // GET /api/produtos/{id}
    @Operation(summary = "Listar produtos por ID", description = "Lista um novo produto no sistema com filtros de ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<ProdutoResponseDTO>> buscarPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);

        return ResponseEntity.ok(ApiSucessResponse.<ProdutoResponseDTO>builder()
                .sucesso(true)
                .mensagem("Produto localizado")
                .dados(produto)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // PUT /api/produtos/{id}
    @Operation(summary = "Atualizar produtos por ID", description = "Atualiza um produto no sistema com novas informações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<ProdutoResponseDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);

        return ResponseEntity.ok(ApiSucessResponse.<ProdutoResponseDTO>builder()
                .sucesso(true)
                .mensagem("Produto atualizado com sucesso")
                .dados(atualizado)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // DELETE /api/produtos/{id}
    @Operation(summary = "Remover produtos", description = "Remover um produto no sistema com alterações parciais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/produtos/{id}/disponibilidade
    @Operation(summary = "Produtos por disponibilidade", description = "Busca produtos no sistema por disponibilidades.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto disponível com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
        produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.noContent().build();
    }

    // GET /api/restaurantes/{restauranteId}/produtos
    @Operation(summary = "Produtos por restaurante", description = "Busca produtos no sistema por restaurante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto por restaurante com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/restaurantes/{restauranteId}/produtos")
    public ResponseEntity<ApiSucessResponse<List<ProdutoResponseDTO>>> listarPorRestaurante(@PathVariable Long restauranteId) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);

        return ResponseEntity.ok(ApiSucessResponse.<List<ProdutoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Produtos do restaurante " + restauranteId)
                .dados(produtos)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/produtos/categoria/{categoria}
    @Operation(summary = "Buscar produtos por categoria", description = "Buscar um produto no sistema por categorias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto por categoria encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiSucessResponse<List<ProdutoResponseDTO>>> buscarPorCategoria(@PathVariable String categoria) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorCategoria(categoria);

        return ResponseEntity.ok(ApiSucessResponse.<List<ProdutoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Produtos encontrados na categoria " + categoria)
                .dados(produtos)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/produtos/buscar?nome={nome}
    @Operation(summary = "Buscar produtos", description = "Buscar um produto no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto ecnontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ApiSucessResponse<List<ProdutoResponseDTO>>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorNome(nome);

        return ResponseEntity.ok(ApiSucessResponse.<List<ProdutoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Resultados da busca por: " + nome)
                .dados(produtos)
                .timestamp(LocalDateTime.now())
                .build());
    }
}