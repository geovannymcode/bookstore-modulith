package com.geovannycode.bookstore.catalog;

import com.geovannycode.bookstore.catalog.command.CreateProductCommand;
import com.geovannycode.bookstore.catalog.command.ProductCommandService;
import com.geovannycode.bookstore.catalog.command.UpdateProductCommand;
import com.geovannycode.bookstore.catalog.query.ProductQueryService;
import com.geovannycode.bookstore.common.models.PagedResult;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * API pública del módulo Catalog.
 *
 * Punto de entrada único para cualquier interacción externa con el catálogo.
 * Quien usa CatalogApi no necesita saber que hay CQRS detrás:
 *   - Las escrituras van a ProductCommandService
 *   - Las lecturas van a ProductQueryService
 */
@Service
public class CatalogApi {

    private final ProductCommandService commandService;
    private final ProductQueryService queryService;

    public CatalogApi(ProductCommandService commandService, ProductQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    // ── Comandos (delegados al Command side) ─────────────────────────

    public Product create(CreateProductCommand command) {
        return commandService.create(command);
    }

    public Product update(String code, UpdateProductCommand command) {
        return commandService.update(code, command);
    }

    // ── Consultas (delegadas al Query side) ──────────────────────────

    public Optional<Product> getByCode(String code) {
        return queryService.findByCode(code);
    }

    public PagedResult<Product> getAll(int page, int size) {
        return queryService.findAll(page, size);
    }

    public List<Product> getByCategory(String category) {
        return queryService.findByCategory(category);
    }

    public List<Product> getTopRated(double minRating) {
        return queryService.findByMinRating(minRating);
    }
}
