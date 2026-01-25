package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.dto.request.*;
import com.deliverytech.delivery_api.dto.response.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.dto.response.ProdutoResponseDTO;
import com.deliverytech.delivery_api.dto.response.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.ClienteService;
import com.deliverytech.delivery_api.service.PedidoService;
import com.deliverytech.delivery_api.service.ProdutoService;
import com.deliverytech.delivery_api.service.RestauranteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;


@Configuration
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final ClienteService clienteService;
    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public DataLoader(ClienteService clienteService,
                      RestauranteService restauranteService,
                      ProdutoService produtoService,
                      PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.restauranteService = restauranteService;
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    @Override
    @Transactional // Garante que a sessão do Hibernate permaneça aberta durante toda a carga
    public void run(String... args) {
        try {
            // VERIFICAÇÃO DE SEGURANÇA: Evita o erro de ObjectOptimisticLockingFailureException
            // Se já existirem clientes, assumimos que a carga já foi feita nesta sessão do banco.
            if (!clienteService.listarClientesAtivos().isEmpty()) {
                logger.info("--- DADOS JÁ PRESENTES NO BANCO. PULANDO CARGA INICIAL PARA EVITAR CONFLITOS ---");
                return;
            }

            logger.info("--- INICIANDO CARGA DE DADOS ---");

            // 1. Inserir Clientes
            ClienteResponseDTO c1 = clienteService.cadastrarCliente(criarClienteDTO("João Silva", "joao@email.com", "(11) 99999-1111", "Rua A, 123"));
            ClienteResponseDTO c2 = clienteService.cadastrarCliente(criarClienteDTO("Maria Santos", "maria@email.com", "(11) 99999-2222", "Rua B, 456"));
            clienteService.cadastrarCliente(criarClienteDTO("Pedro Oliveira", "pedro@email.com", "(11) 99999-3333", "Rua C, 789"));

            // 2. Inserir Restaurantes
            RestauranteResponseDTO r1 = restauranteService.cadastrarRestaurante(criarRestauranteDTO("Pizzaria Bella", "Italiana", "Av. Paulista, 1000", 5.0));
            RestauranteResponseDTO r2 = restauranteService.cadastrarRestaurante(criarRestauranteDTO("Burger House", "Hamburgueria", "Rua Augusta, 500", 3.5));
            RestauranteResponseDTO r3 = restauranteService.cadastrarRestaurante(criarRestauranteDTO("Sushi Master", "Japonesa", "Rua Liberdade, 200", 8.0));

            // 3. Inserir Produtos (Referenciando IDs gerados dinamicamente)
            ProdutoResponseDTO p1 = produtoService.cadastrarProduto(criarProdutoDTO("Pizza Margherita", 35.90, "Pizza", r1.getId(), "Mussarela e manjericão"));
            ProdutoResponseDTO p4 = produtoService.cadastrarProduto(criarProdutoDTO("X-Burger", 18.90, "Hambúrguer", r2.getId(), "Queijo, alface e tomate"));
            ProdutoResponseDTO p7 = produtoService.cadastrarProduto(criarProdutoDTO("Combo Sashimi", 45.90, "Sashimi", r3.getId(), "15 peças variadas"));

            // 4. Inserir Pedidos Iniciais
            pedidoService.criarPedido(criarPedidoDTO(c1.getId(), r1.getId(), p1.getId(), 1));
            pedidoService.criarPedido(criarPedidoDTO(c2.getId(), r2.getId(), p4.getId(), 1));

            executarCenariosDeTeste(c1, c2, r1, r2, r3, p4, p7);

            logger.info("--- CARGA E TESTES CONCLUÍDOS COM SUCESSO ---");

        } catch (Exception e) {
            logger.error("ERRO CRÍTICO NA CARGA DE DADOS: {}", e.getMessage());
            // Não relançamos a exceção para não impedir a aplicação de subir,
            // mas logamos o stacktrace para depuração.
            e.printStackTrace();
        }
    }

    private void executarCenariosDeTeste(ClienteResponseDTO c1, ClienteResponseDTO c2,
                                         RestauranteResponseDTO r1, RestauranteResponseDTO r2,
                                         RestauranteResponseDTO r3, ProdutoResponseDTO p4,
                                         ProdutoResponseDTO p7) {
        logger.info("--- EXECUTANDO CENÁRIOS DE TESTE ---");

        // Cenário 1: Busca por Email
        logger.info("Cenário 1: Cliente encontrado: {}", clienteService.buscarClientePorEmail("joao@email.com").getNome());

        // Cenário 2: Produtos por Restaurante
        logger.info("Cenário 2: Itens no {}: {}", r1.getNome(), produtoService.buscarProdutosPorRestaurante(r1.getId()).size());

        // Cenário 5: Pedido Extra João
        PedidoResponseDTO extra1 = pedidoService.criarPedido(criarPedidoDTO(c1.getId(), r2.getId(), p4.getId(), 2));
        logger.info("Cenário 5: Pedido {} criado para João. Total: R$ {}", extra1.getNumeroPedido(), extra1.getValorTotal());

        // Cenário 6: Pedido Extra Maria
        PedidoResponseDTO extra2 = pedidoService.criarPedido(criarPedidoDTO(c2.getId(), r3.getId(), p7.getId(), 1));
        logger.info("Cenário 6: Pedido {} criado para Maria. Total: R$ {}", extra2.getNumeroPedido(), extra2.getValorTotal());
    }

    // --- MÉTODOS AUXILIARES (Sem alterações, apenas limpeza) ---
    private ClienteRequestDTO criarClienteDTO(String nome, String email, String tel, String end) {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome(nome); dto.setEmail(email); dto.setTelefone(tel); dto.setEndereco(end);
        return dto;
    }

    private RestauranteRequestDTO criarRestauranteDTO(String nome, String cat, String end, Double taxa) {
        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNome(nome); dto.setEndereco(end); dto.setTaxaEntrega(BigDecimal.valueOf(taxa));
        dto.setAtivo(true);
        return dto;
    }

    private ProdutoRequestDTO criarProdutoDTO(String nome, Double preco, String cat, Long restId, String desc) {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome(nome); dto.setPreco(BigDecimal.valueOf(preco)); dto.setCategoria(cat);
        dto.setDescricao(desc); dto.setDisponivel(true); dto.setRestauranteId(restId);
        return dto;
    }

    private PedidoRequestDTO criarPedidoDTO(Long clienteId, Long restId, Long prodId, Integer qtd) {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(clienteId); dto.setRestauranteId(restId);
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(prodId); item.setQuantidade(qtd);
        dto.setItens(List.of(item));
        return dto;
    }
}