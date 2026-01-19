package com.deliverytech.delivery_api.repository;


import com.deliverytech.delivery_api.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidoRespository extends JpaRepository<ItemPedido, Long> {
}
