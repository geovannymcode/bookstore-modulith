package com.geovannycode.bookstore.catalog.query;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Modelo de lectura (Query side) del patrón CQRS.
 *
 * Desnormalizado y optimizado para consultas:
 * - averageRating: precalculado, sin AVG en tiempo de consulta
 * - reviewCount:   precalculado, sin COUNT en tiempo de consulta
 *
 * Se sincroniza automáticamente con el write model cuando
 * CatalogEventHandler procesa los eventos de ProductCreated/ProductUpdated.
 */
@Entity
@Table(name = "product_views", schema = "catalog")
public class ProductView {

    @Id
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private double averageRating = 0.0;

    @Column(nullable = false)
    private int reviewCount = 0;

    private Instant lastUpdatedAt;

    protected ProductView() {}

    public ProductView(String code, String name, String description,
                       String imageUrl, BigDecimal price, String category) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.lastUpdatedAt = Instant.now();
    }

    /**
     * Actualiza el rating promedio al agregar un nuevo review.
     *
     * Esta lógica vive en el read model porque es lógica de PROYECCIÓN,
     * no lógica de dominio. El dominio solo sabe que alguien hizo un review
     * con un rating. El read model decide cómo almacenarlo eficientemente.
     */
    public void addReview(double rating) {
        double totalRating = this.averageRating * this.reviewCount + rating;
        this.reviewCount++;
        this.averageRating = totalRating / this.reviewCount;
        this.lastUpdatedAt = Instant.now();
    }

    public void updateFrom(String name, String description,
                           String imageUrl, BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.lastUpdatedAt = Instant.now();
    }

    // Getters públicos (ProductQueryService los necesita)
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
}