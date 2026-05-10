package com.geovannycode.bookstore.catalog.web;

import com.geovannycode.bookstore.catalog.command.ProductEntity;
import com.geovannycode.bookstore.catalog.command.CreateProductRequest;
import com.geovannycode.bookstore.common.models.PagedResult;
import com.geovannycode.bookstore.catalog.command.ProductCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/products")
public class ProductRestController {

    private final ProductCommandService productCommandService;

    public ProductRestController(ProductCommandService productCommandService) {
        this.productCommandService = productCommandService;
    }

    @GetMapping
    PagedResult<ProductEntity> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productCommandService.getAll(page, size);
    }

    @GetMapping("/{code}")
    ResponseEntity<ProductEntity> getByCode(@PathVariable String code) {
        return productCommandService.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    List<ProductEntity> getByCategory(@PathVariable String category) {
        return productCommandService.getByCategory(category);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProductEntity create(@Valid @RequestBody CreateProductRequest request) {
        return productCommandService.create(request);
    }

    @PutMapping("/{code}")
    ProductEntity update(@PathVariable String code,
                         @Valid @RequestBody CreateProductRequest request) {
        return productCommandService.update(code, request);
    }
}
