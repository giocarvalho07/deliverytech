package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.response.ProdutoResponseDTO;
import com.deliverytech.delivery_api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Valid @RequestBody ProdutoRequestDTO dto) {
        return new ResponseEntity<>(produtoService.cadastrarProduto(dto), HttpStatus.CREATED);
    }

    // GET /api/produtos/{id}
    @Operation(summary = "Listar produtos por ID", description = "Lista um novo produto no sistema com filtros de ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    // PUT /api/produtos/{id}
    @Operation(summary = "Atualizar produtos por ID", description = "Atualiza um produto no sistema com novas informações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, dto));
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
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id,
                                                       @RequestParam boolean disponivel) {
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
    public ResponseEntity<List<ProdutoResponseDTO>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorRestaurante(restauranteId));
    }

    // GET /api/produtos/categoria/{categoria}
    @Operation(summary = "Buscar produtos por categoria", description = "Buscar um produto no sistema por categorias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto por categoria encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    // GET /api/produtos/buscar?nome={nome}
    @Operation(summary = "Buscar produtos", description = "Buscar um produto no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto ecnontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorNome(nome));
    }
}