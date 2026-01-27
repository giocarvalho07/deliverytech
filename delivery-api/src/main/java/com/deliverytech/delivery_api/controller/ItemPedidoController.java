package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.ItemPedidoResponseDTO;
import com.deliverytech.delivery_api.service.ItemPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/item-pedidos")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public ResponseEntity<ApiSucessResponse<List<ItemPedidoResponseDTO>>> listarTodos() {
        List<ItemPedidoResponseDTO> itens = itemPedidoService.listarTodos();

        return ResponseEntity.ok(ApiSucessResponse.<List<ItemPedidoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Lista global de itens recuperada")
                .dados(itens)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<ApiSucessResponse<List<ItemPedidoResponseDTO>>> listarPorPedido(@PathVariable Long id) {
        List<ItemPedidoResponseDTO> itens = itemPedidoService.listarPorPedido(id);

        return ResponseEntity.ok(ApiSucessResponse.<List<ItemPedidoResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Itens do pedido " + id + " localizados")
                .dados(itens)
                .timestamp(LocalDateTime.now())
                .build());
    }
}