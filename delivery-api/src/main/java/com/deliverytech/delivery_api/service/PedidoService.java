package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final ModelMapper modelMapper;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
                         RestauranteRepository restauranteRepository, ProdutoRepository produtoRepository,
                         ModelMapper modelMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {
        // Validar Cliente Ativo
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        if (!cliente.isAtivo()) {
            throw new RuntimeException("Pedido negado: Cliente está inativo.");
        }

        // Validar Restaurante
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setEnderecoEntrega(cliente.getEndereco());
        pedido.setTaxaEntrega(restaurante.getTaxaEntrega());
        pedido.setStatus(StatusPedidos.PENDENTE);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Processar Itens
        processarItens(dto, pedido, restaurante);

        // Aciona o método -> Calcular Total Pedido
        pedido.setValorTotal(calcularTotalPedido(pedido.getItens(), pedido.getTaxaEntrega()));

        return modelMapper.map(pedidoRepository.save(pedido), PedidoResponseDTO.class);
    }

    // Método auxiliar privado para organizar a criação do objeto
    private void processarItens(PedidoRequestDTO dto, Pedido pedido, Restaurante restaurante) {
        dto.getItens().forEach(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new RuntimeException("O produto " + produto.getNome() + " não pertence a este restaurante.");
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade())));

            pedido.getItens().add(item);
        });
    }

    // Calcular Total Pedido
    public BigDecimal calcularTotalPedido(List<ItemPedido> itens, BigDecimal taxaEntrega) {
        BigDecimal totalItens = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalItens.add(taxaEntrega);
    }

    // Buscar por Cliente (Histórico)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedidos novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Validar transições (Exemplo: não pode mudar se estiver cancelado)
        if (pedido.getStatus() == StatusPedidos.CANCELADO || pedido.getStatus() == StatusPedidos.ENTREGUE) {
            throw new RuntimeException("Não é possível alterar o status de um pedido finalizado.");
        }

        pedido.setStatus(novoStatus);
        return modelMapper.map(pedidoRepository.save(pedido), PedidoResponseDTO.class);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Apenas se permitido pelo status
        if (pedido.getStatus() != StatusPedidos.PENDENTE) {
            throw new RuntimeException("O pedido só pode ser cancelado enquanto estiver PENDENTE.");
        }

        pedido.setStatus(StatusPedidos.CANCELADO);
        pedidoRepository.save(pedido);
    }


}