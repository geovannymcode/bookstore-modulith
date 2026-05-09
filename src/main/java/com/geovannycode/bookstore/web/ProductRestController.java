package com.geovannycode.bookstore.web;

import com.geovannycode.bookstore.entities.ProductEntity;
import com.geovannycode.bookstore.models.CreateProductRequest;
import com.geovannycode.bookstore.models.PagedResult;
import com.geovannycode.bookstore.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/products")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    PagedResult<ProductEntity> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAll(page, size);
    }

    @GetMapping("/{code}")
    ResponseEntity<ProductEntity> getByCode(@PathVariable String code) {
        return productService.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    List<ProductEntity> getByCategory(@PathVariable String category) {
        return productService.getByCategory(category);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProductEntity create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{code}")
    ProductEntity update(@PathVariable String code,
                         @Valid @RequestBody CreateProductRequest request) {
        return productService.update(code, request);
    }
}
