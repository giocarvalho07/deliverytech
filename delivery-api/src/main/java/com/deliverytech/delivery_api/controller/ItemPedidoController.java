package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.response.ItemPedidoResponseDTO;
import com.deliverytech.delivery_api.service.ItemPedidoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/item-pedidos")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public List<ItemPedidoResponseDTO> listarTodos() {
        return itemPedidoService.listarTodos();
    }

    @GetMapping("/pedido/{id}")
    public List<ItemPedidoResponseDTO> listarPorPedido(@PathVariable Long id) {
        return itemPedidoService.listarPorPedido(id);
    }
}