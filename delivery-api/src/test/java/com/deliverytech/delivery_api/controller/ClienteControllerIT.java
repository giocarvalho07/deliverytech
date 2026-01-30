package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ClienteRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve cadastrar um cliente com sucesso e retornar 201 Created")
    void deveCadastrarClienteComSucesso() throws Exception {
        // AJUSTE: Preenchendo todos os campos obrigatórios identificados no log
        ClienteRequestDTO clienteDTO = new ClienteRequestDTO();
        clienteDTO.setNome("João Silva");
        clienteDTO.setEmail("joao@teste.com");
        clienteDTO.setTelefone("11999999999");
        clienteDTO.setEndereco("Rua das Flores, 123"); // Faltava este campo!

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sucesso").value(true)) // ApiSucessResponse tem 'sucesso'
                .andExpect(jsonPath("$.dados.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cadastrar cliente inválido")
    void deveRetornarErroAoCadastrarClienteInvalido() throws Exception {
        ClienteRequestDTO clienteInvalido = new ClienteRequestDTO(); // Tudo null

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(status().isBadRequest())
                // AJUSTE: Validando os campos do seu ApiErrorResponse conforme o log
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação nos campos"))
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    @DisplayName("Deve listar clientes ativos com paginação")
    void deveListarClientesPaginados() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.dados.conteudo").isArray());
    }
}