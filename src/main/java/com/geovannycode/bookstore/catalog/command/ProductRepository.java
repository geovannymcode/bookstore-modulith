package com.geovannycode.bookstore.catalog.command;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ⚠️ PROBLEMA (para el workshop): este repositorio es PUBLIC y accesible
 * desde cualquier clase de cualquier paquete. En la arquitectura acoplada,
 * OrderService lo inyecta directamente para buscar productos —
 * rompiendo el boundary entre los dominios Order y Catalog.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByCode(String code);

    Page<ProductEntity> findAll(Pageable pageable);

    List<ProductEntity> findByCategory(String category);

    boolean existsByCode(String code);
}
