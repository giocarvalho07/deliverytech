package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByStatus(StatusPedidos status);
    List<Pedido> findTop10ByOrderByDataPedidoDesc();


    @Query("""
            SELECT p FROM Pedido p
            WHERE p.dataPedido  BETWEEN :inicio AND :fim
    """)
    List<Pedido> findByDateTime(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    //Total de vendas (Soma dos valores) por restaurante
    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.restaurante.id = :restauranteId")
    BigDecimal sumTotalVendasByRestaurante(@Param("restauranteId") Long restauranteId);

    // Pedidos com valor total acima de um valor X
    @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valorMinimo")
    List<Pedido> findPedidosComValorMaiorQue(@Param("valorMinimo") BigDecimal valorMinimo);

    // Relatório por período e status
    @Query("SELECT p FROM Pedido p WHERE p.status = :status AND p.dataPedido BETWEEN :inicio AND :fim")
    List<Pedido> findByStatusAndDataPedidoBetween(
            @Param("status") StatusPedidos status,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}