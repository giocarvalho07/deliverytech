package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.dto.request.*;
import com.deliverytech.delivery_api.dto.response.*;
import com.deliverytech.delivery_api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
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
    @Transactional
    public void run(String... args) {
        try {
            // 0. VERIFICAÇÃO DE SEGURANÇA
            // Busca a primeira página; se houver conteúdo, cancela a carga para não duplicar dados
            var clientesExistentes = clienteService.listarClientesAtivosPaginado(PageRequest.of(0, 1));
            if (clientesExistentes != null && !clientesExistentes.getConteudo().isEmpty()) {
                logger.info("--- DADOS JÁ PRESENTES NO BANCO. PULANDO CARGA INICIAL ---");
                return;
            }

            logger.info("--- INICIANDO CARGA DE DADOS ---");

            // 1. INSERIR CLIENTES
            ClienteResponseDTO c1 = clienteService.cadastrarCliente(
                    criarClienteDTO("João Silva", "joao@email.com", "11988887777", "Rua das Flores, 123"));
            ClienteResponseDTO c2 = clienteService.cadastrarCliente(
                    criarClienteDTO("Maria Souza", "maria@email.com", "11977776666", "Av. Central, 456"));

            // 2. INSERIR RESTAURANTES
            RestauranteResponseDTO r1 = restauranteService.cadastrarRestaurante(
                    criarRestauranteDTO("Pizzaria Bella Napoli", "Italiana", "Av. Paulista, 1000", "11991929394", 5.0));
            RestauranteResponseDTO r2 = restauranteService.cadastrarRestaurante(
                    criarRestauranteDTO("Burger House", "Hamburgueria", "Rua Augusta, 500", "11995554444", 3.5));
            RestauranteResponseDTO r3 = restauranteService.cadastrarRestaurante(
                    criarRestauranteDTO("Sushi Master", "Japonesa", "Rua Liberdade, 200", "11993332222", 8.0));

            // 3. INSERIR PRODUTOS (Usando os IDs dos restaurantes criados acima)
            ProdutoResponseDTO p1 = produtoService.cadastrarProduto(
                    criarProdutoDTO("Pizza Margherita", 35.90, "Pizzas", r1.getId(), "Molho de tomate, muçarela e manjericão fresco"));
            ProdutoResponseDTO p4 = produtoService.cadastrarProduto(
                    criarProdutoDTO("X-Burger Clássico", 18.90, "Hambúrgueres", r2.getId(), "Pão artesanal, carne 180g e queijo prato"));
            ProdutoResponseDTO p7 = produtoService.cadastrarProduto(
                    criarProdutoDTO("Combo Sashimi", 45.90, "Japonesa", r3.getId(), "15 peças variadas de salmão e atum"));

            // 4. EXECUTAR CENÁRIOS DE TESTE (Pedidos)
            executarCenariosDeTeste(c1, c2, r1, r2, r3, p1, p4, p7);

            logger.info("--- CARGA E TESTES CONCLUÍDOS COM SUCESSO ---");

        } catch (Exception e) {
            logger.error("ERRO NA CARGA DE DADOS: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void executarCenariosDeTeste(ClienteResponseDTO c1, ClienteResponseDTO c2,
                                         RestauranteResponseDTO r1, RestauranteResponseDTO r2,
                                         RestauranteResponseDTO r3, ProdutoResponseDTO p1,
                                         ProdutoResponseDTO p4, ProdutoResponseDTO p7) {

        logger.info("--- EXECUTANDO CENÁRIOS DE TESTE (CRIAÇÃO DE PEDIDOS) ---");

        // Pedido do João na Pizzaria
        PedidoResponseDTO ped1 = pedidoService.criarPedido(criarPedidoDTO(c1.getId(), r1.getId(), p1.getId(), 1, c1.getEndereco()));
        logger.info("Pedido 1 criado: Número {}, Total R$ {}", ped1.getNumeroPedido(), ped1.getValorTotal());

        // Pedido da Maria no Sushi
        PedidoResponseDTO ped2 = pedidoService.criarPedido(criarPedidoDTO(c2.getId(), r3.getId(), p7.getId(), 1, c2.getEndereco()));
        logger.info("Pedido 2 criado: Número {}, Total R$ {}", ped2.getNumeroPedido(), ped2.getValorTotal());
    }

    // --- MÉTODOS AUXILIARES AJUSTADOS PARA OS SEUS DTOS ---

    private ClienteRequestDTO criarClienteDTO(String nome, String email, String tel, String end) {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setTelefone(tel);
        dto.setEndereco(end);
        return dto;
    }

    private RestauranteRequestDTO criarRestauranteDTO(String nome, String cat, String end, String tel, Double taxa) {
        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNome(nome);
        dto.setCategoria(cat);
        dto.setEndereco(end);
        dto.setTelefone(tel);
        dto.setTaxaEntrega(BigDecimal.valueOf(taxa));
        dto.setAtivo(true);
        return dto;
    }

    private ProdutoRequestDTO criarProdutoDTO(String nome, Double preco, String cat, Long restId, String desc) {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome(nome);
        dto.setPreco(BigDecimal.valueOf(preco));
        dto.setCategoria(cat);
        dto.setDescricao(desc);
        dto.setDisponivel(true);
        dto.setRestauranteId(restId);
        return dto;
    }

    private PedidoRequestDTO criarPedidoDTO(Long clienteId, Long restId, Long prodId, Integer qtd, String endereco) {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(clienteId);
        dto.setRestauranteId(restId);
        dto.setEnderecoEntrega(endereco); // Campo obrigatório no seu PedidoRequestDTO

        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(prodId);
        item.setQuantidade(qtd);

        dto.setItens(List.of(item));
        return dto;
    }
}