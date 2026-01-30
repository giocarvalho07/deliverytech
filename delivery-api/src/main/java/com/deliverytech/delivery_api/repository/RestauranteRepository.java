package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.model.Restaurante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    List<Restaurante> findByAtivoTrue();
    List<Restaurante> findByCategoria(String categoria);
    List<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc();
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxaEntrega);

    @Query(value = "SELECT * FROM restaurantes r WHERE " +
            "(:categoria IS NULL OR LOWER(r.categoria) = LOWER(:categoria)) AND " +
            "(:ativo IS NULL OR r.ativo = :ativo)",
            countQuery = "SELECT count(*) FROM restaurantes r WHERE " +
                    "(:categoria IS NULL OR LOWER(r.categoria) = LOWER(:categoria)) AND " +
                    "(:ativo IS NULL OR r.ativo = :ativo)",
            nativeQuery = true)
    Page<Restaurante> listarComFiltros(String categoria, Boolean ativo, Pageable pageable);
}
