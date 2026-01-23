package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByRestauranteId(Long restauranteId);
    List<Produto> findByCategoria(String categoria);
    List<Produto> findByDisponivelTrue();

    // Produtos mais vendidos (Baseado na tabela de itens_pedido)
    @Query(value = "SELECT p.*, COUNT(ip.id) as total_vendas " +
            "FROM produtos p " +
            "JOIN itens_pedido ip ON p.id = ip.produto_id " +
            "GROUP BY p.id " +
            "ORDER BY total_vendas DESC LIMIT 5",
            nativeQuery = true)
    List<Produto> findTop5ProdutosMaisVendidos();

    // Faturamento por categoria de produto
    @Query(value = "SELECT p.categoria, SUM(ip.subtotal) as faturamento " +
            "FROM produtos p " +
            "JOIN itens_pedido ip ON p.id = ip.produto_id " +
            "GROUP BY p.categoria",
            nativeQuery = true)
    List<Object[]> findFaturamentoPorCategoria();
}
