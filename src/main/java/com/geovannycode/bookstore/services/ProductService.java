package com.geovannycode.bookstore.services;

import com.geovannycode.bookstore.exceptions.ProductAlreadyExistsException;
import com.geovannycode.bookstore.exceptions.ProductNotFoundException;
import com.geovannycode.bookstore.entities.ProductEntity;
import com.geovannycode.bookstore.models.CreateProductRequest;
import com.geovannycode.bookstore.models.PagedResult;
import com.geovannycode.bookstore.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio del catálogo de productos.
 *
 * ⚠️ PROBLEMAS (para el workshop):
 *
 * 1. SIN CQRS: una sola tabla sirve para lectura y escritura.
 *    Las consultas de rating y reviews requerirían JOINs costosos.
 *
 * 2. SIN API PÚBLICA DEFINIDA: cualquier servicio puede inyectar
 *    ProductRepository directamente, como hace OrderService.
 *    No hay un contrato explícito de qué puede hacer el exterior.
 *
 * Objetivo: separar en ProductCommandService (escritura) y
 * ProductQueryService (lectura), con CatalogApi como contrato público.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductEntity create(CreateProductRequest request) {
        if (productRepository.existsByCode(request.code())) {
            throw new ProductAlreadyExistsException(request.code());
        }

        var entity = new ProductEntity(
                request.code(), request.name(), request.description(),
                request.imageUrl(), request.price(), request.category()
        );

        var saved = productRepository.save(entity);
        log.info("Producto creado: code={}", saved.getCode());
        return saved;
    }

    public ProductEntity update(String code, CreateProductRequest request) {
        var entity = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));

        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setImageUrl(request.imageUrl());
        entity.setPrice(request.price());
        entity.setCategory(request.category());

        return productRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Optional<ProductEntity> getByCode(String code) {
        return productRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public PagedResult<ProductEntity> getAll(int page, int size) {
        var pageable = PageRequest.of(
                Math.max(0, page - 1), size,
                Sort.by("name")
        );
        return PagedResult.of(productRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public List<ProductEntity> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
