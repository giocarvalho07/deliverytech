package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ItemPedidoRequestDTO;
import com.deliverytech.delivery_api.dto.request.PedidoRequestDTO;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
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
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PedidoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // Repositories para preparar o cenário
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoRepository pedidoRepository;

    private Cliente clienteSalvo;
    private Restaurante restauranteSalvo;
    private Produto produtoSalvo;
    private Pedido pedidoSalvo;

    @BeforeEach
    void setUp() {
        // 1. Criar Cliente
        Cliente c = new Cliente();
        c.setNome("João Silva");
        c.setEmail("joao@teste.com");
        c.setTelefone("1199999999");
        c.setEndereco("Rua A");
        c.setAtivo(true);
        clienteSalvo = clienteRepository.save(c);

        // 2. Criar Restaurante
        Restaurante r = new Restaurante();
        r.setNome("Burger King");
        r.setAtivo(true);
        r.setTaxaEntrega(BigDecimal.valueOf(5.0));
        restauranteSalvo = restauranteRepository.save(r);

        // 3. Criar Produto
        Produto p = new Produto();
        p.setNome("Whopper");
        p.setPreco(BigDecimal.valueOf(30.0));
        p.setRestaurante(restauranteSalvo);
        p.setDisponivel(true); // <--- ADICIONE ESTA LINHA AQUI!
        p.setCategoria("Hambúrguer"); // Recomendo setar categoria também por segurança
        produtoSalvo = produtoRepository.save(p);

        // 4. Criar um Pedido para testes de busca/atualização
        Pedido ped = new Pedido();
        ped.setCliente(clienteSalvo);
        ped.setRestaurante(restauranteSalvo);
        ped.setEnderecoEntrega("Rua de Entrega, 123");
        ped.setStatus(com.deliverytech.delivery_api.enums.StatusPedidos.PENDENTE);
        ped.setValorTotal(BigDecimal.valueOf(35.0));
        pedidoSalvo = pedidoRepository.save(ped);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Criar pedido com sucesso")
    void deveCriarPedidoCompleto() throws Exception {
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produtoSalvo.getId());
        item.setQuantidade(2);

        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setClienteId(clienteSalvo.getId());
        pedidoDTO.setRestauranteId(restauranteSalvo.getId());
        pedidoDTO.setEnderecoEntrega("Av Paulista, 1000"); // CORREÇÃO: Campo obrigatório
        pedidoDTO.setItens(List.of(item));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sucesso").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Atualizar status do pedido")
    void deveAtualizarStatusPedido() throws Exception {
        mockMvc.perform(patch("/api/pedidos/" + pedidoSalvo.getId() + "/status")
                        .param("status", "ENTREGUE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE", "ADMIN"})
    @DisplayName("Cenário: Buscar histórico de pedidos do cliente")
    void deveBuscarHistoricoPaginado() throws Exception {
        mockMvc.perform(get("/api/pedidos/clientes/" + clienteSalvo.getId() + "/pedidos")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));
    }
}