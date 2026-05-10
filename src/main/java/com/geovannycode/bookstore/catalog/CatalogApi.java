package com.geovannycode.bookstore.catalog;

import com.geovannycode.bookstore.catalog.command.ProductCommandService;
import com.geovannycode.bookstore.catalog.command.ProductEntity;
import com.geovannycode.bookstore.common.models.PagedResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Catalog.
 *
 * Único punto de entrada para que otros módulos interactúen con catalog.
 * Hoy delega a ProductCommandService. En la Parte 3 también delegará
 * a ProductQueryService cuando implementemos CQRS.
 *
 *   orders → CatalogApi.getByCode()     ✅
 *   orders → ProductCommandService      ❌ violación de boundary
 *   orders → ProductRepository          ❌ violación grave
 */
@Service
public class CatalogApi {

    private final ProductCommandService productService;

    public CatalogApi(ProductCommandService productService) {
        this.productService = productService;
    }

    public Optional<Product> getByCode(String code) {
        return productService.getByCode(code).map(this::toProduct);
    }

    public List<Product> getByCategory(String category) {
        return productService.getByCategory(category)
                .stream()
                .map(this::toProduct)
                .toList();
    }

    /**
     * Convierte ProductEntity (privado) a Product (público).
     * Este mapper vive aquí porque es responsabilidad de catalog
     * decidir qué expone al exterior — no de quien consume el API.
     */
    private Product toProduct(ProductEntity entity) {
        return new Product(
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getPrice(),
                entity.getCategory()
        );
    }
}