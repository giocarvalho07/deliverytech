package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.response.ItemPedidoResponseDTO;
import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.repository.ItemPedidoRespository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ItemPedidoService {

    private final ItemPedidoRespository itemPedidoRepository;
    private final ModelMapper modelMapper;

    public ItemPedidoService(ItemPedidoRespository itemPedidoRepository, ModelMapper modelMapper) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.modelMapper = modelMapper;
    }

    public List<ItemPedidoResponseDTO> listarTodos() {
        return itemPedidoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ItemPedidoResponseDTO> listarPorPedido(Long pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ItemPedidoResponseDTO convertToDto(ItemPedido item) {
        ItemPedidoResponseDTO dto = modelMapper.map(item, ItemPedidoResponseDTO.class);
        // Garante que o nome do produto seja mapeado corretamente se o ModelMapper falhar implicitamente
        if (item.getProduto() != null) {
            dto.setNomeProduto(item.getProduto().getNome());
        }
        return dto;
    }
}