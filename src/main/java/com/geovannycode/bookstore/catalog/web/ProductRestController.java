package com.geovannycode.bookstore.catalog.web;

import com.geovannycode.bookstore.catalog.CatalogApi;
import com.geovannycode.bookstore.catalog.Product;
import com.geovannycode.bookstore.catalog.command.CreateProductCommand;
import com.geovannycode.bookstore.catalog.command.UpdateProductCommand;
import com.geovannycode.bookstore.common.models.PagedResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/products")
public class ProductRestController {

    private final CatalogApi catalogApi;

    ProductRestController(CatalogApi catalogApi) {
        this.catalogApi = catalogApi;
    }

    @GetMapping
    PagedResult<Product> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return catalogApi.getAll(page, size);
    }

    @GetMapping("/{code}")
    ResponseEntity<Product> getByCode(@PathVariable String code) {
        return catalogApi.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    List<Product> getByCategory(@PathVariable String category) {
        return catalogApi.getByCategory(category);
    }

    @GetMapping("/top-rated")
    List<Product> getTopRated(
            @RequestParam(defaultValue = "4.0") double minRating) {
        return catalogApi.getTopRated(minRating);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Product create(@Valid @RequestBody CreateProductCommand command) {
        return catalogApi.create(command);
    }

    @PutMapping("/{code}")
    Product update(@PathVariable String code,
                   @Valid @RequestBody UpdateProductCommand command) {
        return catalogApi.update(code, command);
    }
}
