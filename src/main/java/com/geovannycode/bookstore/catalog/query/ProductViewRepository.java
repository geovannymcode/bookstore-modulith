package com.geovannycode.bookstore.catalog.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de lectura del módulo Catalog.
 *
 * Solo lee de catalog.product_views — nunca toca catalog.products.
 * Esa separación es la esencia del Query side en CQRS.
 */
public interface ProductViewRepository extends JpaRepository<ProductView, String> {

    Optional<ProductView> findByCode(String code);

    Page<ProductView> findAll(Pageable pageable);

    List<ProductView> findByCategory(String category);

    /**
     * Retorna productos con rating mayor o igual al mínimo,
     * ordenados de mayor a menor rating.
     * Sin CQRS, esta consulta requeriría un AVG + GROUP BY en cada llamada.
     * Con el read model, es un simple WHERE + ORDER BY.
     */
    List<ProductView> findByAverageRatingGreaterThanEqualOrderByAverageRatingDesc(
            double minRating);
}