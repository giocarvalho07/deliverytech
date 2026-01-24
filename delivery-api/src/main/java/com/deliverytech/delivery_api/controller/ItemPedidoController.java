package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.service.ItemPedidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item-pedidos") // Este comando "mata" o erro 404
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    // Endpoint para listar todos (Teste: http://localhost:8080/item-pedidos)
    @GetMapping
    public List<ItemPedido> listarTodos() {
        return itemPedidoService.listarTodos();
    }

    // Endpoint para buscar por ID do pedido (Ex: http://localhost:8080/item-pedidos/pedido/1)
    @GetMapping("/pedido/{id}")
    public List<ItemPedido> listarPorPedido(@PathVariable Long id) {
        return itemPedidoService.listarPorPedido(id);
    }
}