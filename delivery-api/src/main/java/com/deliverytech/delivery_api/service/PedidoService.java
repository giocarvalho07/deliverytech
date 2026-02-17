package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.response.PagedResponse;
import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.exeption.BusinessException;
import com.deliverytech.delivery_api.exeption.EntityNotFoundException;
import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + dto.getClienteId()));

            if (!cliente.isAtivo()) {
                throw new BusinessException("Pedido negado: O cliente selecionado está inativo.");
            }

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

            processarItens(dto, pedido, restaurante);
            pedido.setValorTotal(calcularTotalPedido(pedido.getItens(), pedido.getTaxaEntrega()));

            return modelMapper.map(pedidoRepository.save(pedido), PedidoResponseDTO.class);

        } catch (EntityNotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new TransactionException("Erro interno ao processar a transação do pedido: " + e.getMessage()) {};
        }
    }

    private void processarItens(PedidoRequestDTO dto, Pedido pedido, Restaurante restaurante) {
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new BusinessException("Um pedido deve conter pelo menos um item.");
        }

        dto.getItens().forEach(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto ID " + itemDto.getProdutoId() + " não existe."));

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("O produto " + produto.getNome() + " não pertence ao restaurante " + restaurante.getNome());
            }

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

    // NOVO: POST /api/pedidos/calcular (Simulação sem persistência)
    public BigDecimal calcularSimulacao(PedidoRequestDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        BigDecimal totalItens = dto.getItens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
            return produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalItens.add(restaurante.getTaxaEntrega());
    }

    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido ID " + id + " não encontrado."));
        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    // NOVO: GET /api/pedidos (Listagem com filtros e paginação)
    public PagedResponse<PedidoResponseDTO> listarComFiltrosPaginado(StatusPedidos status, LocalDateTime data, Pageable pageable) {

        // Busca do banco já trazendo apenas os registros da página (ex: 10 itens)
        Page<Pedido> paginaEntidades = pedidoRepository.findWithFilters(status, data, pageable);
        // Converte a página de entidades para DTOs
        Page<PedidoResponseDTO> paginaDtos = paginaEntidades
                .map(p -> modelMapper.map(p, PedidoResponseDTO.class));
        // Envelopa no seu DTO de paginação
        return new PagedResponse<>(paginaDtos);
    }

    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("Cliente ID " + clienteId + " não encontrado.");
        }
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(p -> modelMapper.map(p, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }

    // NOVO: GET /api/restaurantes/{restauranteId}/pedidos
    public List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante ID " + restauranteId + " não encontrado.");
        }
        return pedidoRepository.findByRestauranteId(restauranteId).stream()
                .map(p -> modelMapper.map(p, PedidoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedidos novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não localizado para atualização."));

        if (pedido.getStatus() == StatusPedidos.CANCELADO || pedido.getStatus() == StatusPedidos.ENTREGUE) {
            throw new BusinessException("Não é possível alterar o status de um pedido já finalizado.");
        }

        pedido.setStatus(novoStatus);
        return modelMapper.map(pedidoRepository.save(pedido), PedidoResponseDTO.class);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não localizado para cancelamento."));

        if (pedido.getStatus() != StatusPedidos.PENDENTE) {
            throw new BusinessException("O cancelamento só é permitido para pedidos com status PENDENTE.");
        }

        pedido.setStatus(StatusPedidos.CANCELADO);
        pedidoRepository.save(pedido);
    }
}