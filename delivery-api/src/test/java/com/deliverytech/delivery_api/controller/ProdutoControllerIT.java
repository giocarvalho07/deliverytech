package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ProdutoRequestDTO;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProdutoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private RestauranteRepository restauranteRepository;

    private Restaurante restauranteSalvo;
    private Produto produtoSalvo;

    @BeforeEach
    void setUp() {
        // 1. Criar Restaurante (Pai do Produto)
        Restaurante r = new Restaurante();
        r.setNome("Restaurante Teste");
        r.setAtivo(true);
        r.setTaxaEntrega(BigDecimal.valueOf(10.0));
        restauranteSalvo = restauranteRepository.save(r);

        // 2. Criar Produto para testes de Busca, Atualização e Exclusão
        Produto p = new Produto();
        p.setNome("Produto Existente");
        p.setDescricao("Descrição Original");
        p.setPreco(BigDecimal.valueOf(50.0));
        p.setCategoria("Lanches");
        p.setDisponivel(true);
        p.setRestaurante(restauranteSalvo);
        produtoSalvo = produtoRepository.save(p);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Cadastrar produto com sucesso")
    void deveCriarProduto() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome("Novo Hambúrguer");
        dto.setDescricao("Pão, carne e queijo");
        dto.setPreco(BigDecimal.valueOf(35.0));
        dto.setCategoria("Artesanal"); // CORREÇÃO: Campo obrigatório
        dto.setDisponivel(true);       // CORREÇÃO: Campo obrigatório
        dto.setRestauranteId(restauranteSalvo.getId());

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dados.nome").value("Novo Hambúrguer"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Buscar produto por ID")
    void deveBuscarProduto() throws Exception {
        mockMvc.perform(get("/api/produtos/" + produtoSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.id").value(produtoSalvo.getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Atualizar produto")
    void deveAtualizarProduto() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome("Produto Atualizado");
        dto.setDescricao("Nova Descrição"); // CORREÇÃO: Campo obrigatório
        dto.setPreco(BigDecimal.valueOf(99.99));
        dto.setCategoria("Lanches");       // CORREÇÃO: Campo obrigatório
        dto.setDisponivel(false);          // CORREÇÃO: Campo obrigatório
        dto.setRestauranteId(restauranteSalvo.getId());

        mockMvc.perform(put("/api/produtos/" + produtoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.nome").value("Produto Atualizado"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Excluir produto")
    void deveExcluirProduto() throws Exception {
        mockMvc.perform(delete("/api/produtos/" + produtoSalvo.getId()))
                .andExpect(status().isNoContent());

        // Valida se realmente sumiu
        mockMvc.perform(get("/api/produtos/" + produtoSalvo.getId()))
                .andExpect(status().isNotFound());
    }
}