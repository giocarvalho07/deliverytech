package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.exepction.BusinessException;
import com.deliverytech.delivery_api.exepction.EntityNotFoundException;
import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
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
        try {
            // Validar Cliente Ativo
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + dto.getClienteId()));

            if (!cliente.isAtivo()) {
                throw new BusinessException("Pedido negado: O cliente selecionado está inativo.");
            }

            // Validar Restaurante
            Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + dto.getRestauranteId()));

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setRestaurante(restaurante);
            pedido.setEnderecoEntrega(cliente.getEndereco());
            pedido.setTaxaEntrega(restaurante.getTaxaEntrega());
            pedido.setStatus(StatusPedidos.PENDENTE);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // Processar Itens e Validar disponibilidade/pertencimento
            processarItens(dto, pedido, restaurante);

            // Calcular Total Pedido
            pedido.setValorTotal(calcularTotalPedido(pedido.getItens(), pedido.getTaxaEntrega()));

            return modelMapper.map(pedidoRepository.save(pedido), PedidoResponseDTO.class);

        } catch (EntityNotFoundException | BusinessException e) {
            throw e; // Re-lança exceções de negócio para serem tratadas pelo Handler
        } catch (Exception e) {
            throw new TransactionException("Erro interno ao processar a transação do pedido: " + e.getMessage()) {
            };
        }
    }

    private void processarItens(PedidoRequestDTO dto, Pedido pedido, Restaurante restaurante) {
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new BusinessException("Um pedido deve conter pelo menos um item.");
        }

        dto.getItens().forEach(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto ID " + itemDto.getProdutoId() + " não existe."));

            // Validar se produto pertence ao restaurante
            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("O produto " + produto.getNome() + " não pertence ao restaurante " + restaurante.getNome());
            }

            // Validar disponibilidade
            if (!produto.getDisponivel()) {
                throw new BusinessException("O produto " + produto.getNome() + " não está disponível para pedidos.");
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

    public BigDecimal calcularTotalPedido(List<ItemPedido> itens, BigDecimal taxaEntrega) {
        BigDecimal totalItens = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalItens.add(taxaEntrega);
    }

    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("Cliente ID " + clienteId + " não encontrado.");
        }
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido ID " + id + " não encontrado."));
        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedidos novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não localizado para atualização."));

        // Validar transições permitidas
        if (pedido.getStatus() == StatusPedidos.CANCELADO || pedido.getStatus() == StatusPedidos.ENTREGUE) {
            throw new BusinessException("Não é possível alterar o status de um pedido já finalizado (" + pedido.getStatus() + ").");
        }

        pedido.setStatus(novoStatus);
        return modelMapper.map(pedidoRepository.save(pedido), PedidoResponseDTO.class);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não localizado para cancelamento."));

        // Validar se cancelamento é permitido pelo status
        if (pedido.getStatus() != StatusPedidos.PENDENTE) {
            throw new BusinessException("O cancelamento só é permitido para pedidos com status PENDENTE.");
        }

        pedido.setStatus(StatusPedidos.CANCELADO);
        pedidoRepository.save(pedido);
    }
}