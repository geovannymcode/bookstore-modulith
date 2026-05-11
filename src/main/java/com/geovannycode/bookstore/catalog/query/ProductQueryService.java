package com.geovannycode.bookstore.catalog.query;

import com.geovannycode.bookstore.catalog.Product;
import com.geovannycode.bookstore.common.models.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lectura del módulo Catalog (Query side).
 *
 * Solo consulta product_views. Nunca toca catalog.products.
 * Si en el futuro necesitas escalar lectura independientemente de escritura,
 * este servicio puede apuntar a una réplica de PostgreSQL sin tocar
 * una sola línea de ProductCommandService.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductViewRepository viewRepository;

    public ProductQueryService(ProductViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    public Optional<Product> findByCode(String code) {
        return viewRepository.findByCode(code).map(this::toProduct);
    }

    public PagedResult<Product> findAll(int page, int size) {
        var pageable = PageRequest.of(
                Math.max(0, page - 1), size,
                Sort.by("name")
        );
        return PagedResult.of(viewRepository.findAll(pageable).map(this::toProduct));
    }

    public List<Product> findByCategory(String category) {
        return viewRepository.findByCategory(category)
                .stream()
                .map(this::toProduct)
                .toList();
    }

    public List<Product> findByMinRating(double minRating) {
        return viewRepository
                .findByAverageRatingGreaterThanEqualOrderByAverageRatingDesc(minRating)
                .stream()
                .map(this::toProduct)
                .toList();
    }

    private Product toProduct(ProductView view) {
        return new Product(
                view.getCode(), view.getName(), view.getDescription(),
                view.getImageUrl(), view.getPrice(), view.getCategory(),
                view.getAverageRating(), view.getReviewCount()
        );
    }
}