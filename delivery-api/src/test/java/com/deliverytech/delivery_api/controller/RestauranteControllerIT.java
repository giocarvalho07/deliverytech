package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestauranteRepository restauranteRepository; // Adicionado para preparar o banco

    @BeforeEach
    void setUp() {
        // Garantimos que existe pelo menos um restaurante com a categoria "Pizza" para o teste
        Restaurante r = new Restaurante();
        r.setNome("Pizza Teste");
        r.setCategoria("Pizza");
        r.setAtivo(true);
        r.setTaxaEntrega(BigDecimal.TEN);
        restauranteRepository.save(r);
    }

    @Test
    @DisplayName("Deve filtrar restaurantes por categoria e status ativo")
    void deveFiltrarRestaurantes() throws Exception {
        String categoriaFiltro = "Pizza";

        mockMvc.perform(get("/api/restaurantes")
                        .param("categoria", categoriaFiltro)
                        .param("ativo", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                // Agora o conteúdo não estará vazio
                .andExpect(jsonPath("$.dados.conteudo[0].categoria").value(categoriaFiltro));
    }

    @Test
    @DisplayName("Deve listar restaurantes com paginação padrão")
    void deveListarRestaurantesPaginados() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.conteudo").isNotEmpty());
    }

    @Test
    @DisplayName("Deve retornar página vazia para filtros inexistentes")
    void deveRetornarVazioParaFiltroInexistente() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                        .param("categoria", "ChurrascoGrego")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.conteudo").isEmpty());
    }
}