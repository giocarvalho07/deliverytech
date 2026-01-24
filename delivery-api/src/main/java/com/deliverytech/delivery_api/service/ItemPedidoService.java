package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.model.ItemPedido;
import com.deliverytech.delivery_api.repository.ItemPedidoRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemPedidoService {

    @Autowired
    private ItemPedidoRespository itemPedidoRepository;

    public ItemPedidoService(ItemPedidoRespository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<ItemPedido> listarTodos() {
        return itemPedidoRepository.findAll();
    }

    public List<ItemPedido> listarPorPedido(Long pedidoId){
        return itemPedidoRepository.findByPedidoId(pedidoId);
    }
}