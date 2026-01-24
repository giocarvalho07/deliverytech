package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRespository itemPedidoRespository;

    public DataLoader(ClienteRepository clienteRepository,
                      RestauranteRepository restauranteRepository,
                      ProdutoRepository produtoRepository,
                      PedidoRepository pedidoRepository,
                     ItemPedidoRespository itemPedidoRespository ) {
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRespository = itemPedidoRespository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Iniciando a carga de dados de teste...");

        // 1. Inserir Clientes
        Cliente c1 = criarCliente("João Silva", "joao@email.com", "(11) 99999-1111", "Rua A, 123 - São Paulo/SP");
        Cliente c2 = criarCliente("Maria Santos", "maria@email.com", "(11) 99999-2222", "Rua B, 456 - São Paulo/SP");
        Cliente c3 = criarCliente("Pedro Oliveira", "pedro@email.com", "(11) 99999-3333", "Rua C, 789 - São Paulo/SP");
        clienteRepository.saveAll(Arrays.asList(c1, c2, c3));

        // 2. Inserir Restaurantes
        Restaurante r1 = criarRestaurante("Pizzaria Bella", "Italiana", "Av. Paulista, 1000", "(11) 3333-1111", 5.0, 4.5);
        Restaurante r2 = criarRestaurante("Burger House", "Hamburgueria", "Rua Augusta, 500", "(11) 3333-2222", 3.5, 4.2);
        Restaurante r3 = criarRestaurante("Sushi Master", "Japonesa", "Rua Liberdade, 200", "(11) 3333-3333", 8.0, 4.8);
        restauranteRepository.saveAll(Arrays.asList(r1, r2, r3));

        // 3. Inserir Produtos
        Produto p1 = criarProduto("Pizza Margherita", 35.90, "Pizza", r1);
        Produto p4 = criarProduto("X-Burger", 18.90, "Hambúrguer", r2);
        Produto p7 = criarProduto("Combo Sashimi", 45.90, "Sashimi", r3);
        produtoRepository.saveAll(Arrays.asList(p1, p4, p7));

        // 4. Inserir Pedidos
        Pedido ped1 = criarPedido("PED1234567890", StatusPedidos.PENDENTE, 5.0, 40.90, c1, r1);
        Pedido ped2 = criarPedido("PED1234567891", StatusPedidos.CONFIRMADO, 3.5, 22.40, c2, r2);
        Pedido ped3 = criarPedido("PED1234567892", StatusPedidos.ENTREGUE, 8.0, 53.90, c3, r3);
        pedidoRepository.saveAll(Arrays.asList(ped1, ped2, ped3));


        // 5. Inserir Itens dos Pedidos
        ItemPedido item1 = criarItemPedido(1, 35.90, ped1, p1); // Pizza no Pedido 1
        ItemPedido item2 = criarItemPedido(1, 18.90, ped2, p4); // Burger no Pedido 2
        ItemPedido item3 = criarItemPedido(1, 45.90, ped3, p7); // Sushi no Pedido 3

        itemPedidoRespository.saveAll(Arrays.asList(item1, item2, item3));

        logger.info("Carga de dados finalizada com sucesso!");
        logger.info("Clientes cadastrados: {}", clienteRepository.count());
        logger.info("Restaurantes cadastrados: {}", restauranteRepository.count());
        logger.info("Produtos cadastrados: {}", produtoRepository.count());
        logger.info("Pedidos cadastrados: {}", pedidoRepository.count());
        logger.info("Itens Pedidos cadastrados: {}", itemPedidoRespository.count());



        logger.info("---------- EXECUTANDO CENÁRIOS DE TESTE 2----------");

        // 🔎 Cenário 1: Busca de Cliente por Email
        clienteRepository.findByEmail("joao@email.com").ifPresent(c ->
                logger.info("Cenário 1 (Email) - Sucesso: Cliente encontrado: {}", c.getNome())
        );

        // 🍔 Cenário 2: Produtos por Restaurante
        List<Produto> produtos = produtoRepository.findByRestauranteId(1L);
        logger.info("Cenário 2 (Produtos) - Sucesso: Itens encontrados: {}", produtos.size());

        // 📅 Cenário 3: Pedidos Recentes
        List<Pedido> pedidosRecentes = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        logger.info("Cenário 3 (Pedidos) - Sucesso: Pedidos recuperados: {}", pedidosRecentes.size());

        // 💰 Cenário 4: Restaurantes por Taxa
        List<Restaurante> baratos = restauranteRepository.findByTaxaEntregaLessThanEqual(BigDecimal.valueOf(5.00));
        logger.info("Cenário 4 (Taxa) - Sucesso: Restaurantes com taxa <= 5.00: {}", baratos.size());

        logger.info("--------------------------------------------------");
    }

    // Métodos auxiliares para limpar o código (usando Setters para evitar erro de construtor)

    private Cliente criarCliente(String nome, String email, String tel, String end) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone(tel);
        c.setEndereco(end);
        c.setDataCadastro(LocalDateTime.now());
        c.setAtivo(true);
        return c;
    }

    private Restaurante criarRestaurante(String nome, String cat, String end, String tel, Double taxa, Double aval) {
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setCategoria(cat);
        r.setEndereco(end);
        r.setTelefone(tel);

        // Converte a taxa de entrega para BigDecimal
        r.setTaxaEntrega(BigDecimal.valueOf(taxa));

        // Converte a avaliação para BigDecimal (ajuste para o erro da linha 95)
        r.setAvaliacao(BigDecimal.valueOf(aval));

        r.setAtivo(true);
        return r;
    }

    private Produto criarProduto(String nome, Double preco, String cat, Restaurante rest) {
        Produto p = new Produto();
        p.setNome(nome);
        // CONVERSÃO AQUI: Double para BigDecimal
        p.setPreco(BigDecimal.valueOf(preco));
        p.setCategoria(cat);
        p.setRestaurante(rest);
        p.setDisponivel(true);
        return p;
    }

    private Pedido criarPedido(String num, StatusPedidos status, Double taxa, Double total, Cliente c, Restaurante r) {
        Pedido p = new Pedido();
        p.setNumeroPedido(num);
        p.setStatus(status);
        p.setDataPedido(LocalDateTime.now());
        // CONVERSÕES AQUI: Double para BigDecimal
        p.setTaxaEntrega(BigDecimal.valueOf(taxa));
        p.setValorTotal(BigDecimal.valueOf(total));
        p.setEnderecoEntrega(c.getEndereco());
        p.setCliente(c);
        p.setRestaurante(r);
        return p;
    }

    private ItemPedido criarItemPedido(Integer qtd, Double precoUnit, Pedido ped, Produto prod) {
        ItemPedido item = new ItemPedido();
        item.setQuantidade(qtd);
        item.setPrecoUnitario(BigDecimal.valueOf(precoUnit));
        // Calcula o subtotal automaticamente: qtd * precoUnit
        BigDecimal subtotal = BigDecimal.valueOf(precoUnit).multiply(BigDecimal.valueOf(qtd));
        item.setSubtotal(subtotal);
        item.setPedido(ped);
        item.setProduto(prod);
        return item;
    }
}