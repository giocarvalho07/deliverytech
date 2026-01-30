package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Page<Cliente> findByAtivoTrue(Pageable pageable);

    // Ranking de clientes por nº de pedidos
    @Query(value = "SELECT c.nome, COUNT(p.id) as total_pedidos " +
            "FROM clientes c " +
            "LEFT JOIN pedidos p ON c.id = p.cliente_id " +
            "GROUP BY c.id, c.nome " +
            "ORDER BY total_pedidos DESC",
            nativeQuery = true)
    List<Object[]> findRankingClientesPorPedidos();
}
