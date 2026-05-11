package com.geovannycode.bookstore.catalog.command;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Modelo de escritura (Command side) del patrón CQRS.
 *
 * Optimizado para consistencia: normalizado, con constraints, sin campos calculados.
 * Solo ProductCommandService lo usa — es package-private intencionalmente.
 *
 * La clase es package-private porque NADIE fuera de catalog.command
 * debería instanciar o inyectar esta entidad directamente.
 */
@Entity
@Table(name = "products", schema = "catalog")
class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalog_product_seq")
    @SequenceGenerator(
            name = "catalog_product_seq",
            sequenceName = "catalog.product_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(nullable = false, unique = true)
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

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onPrePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    void onPreUpdate() {
        this.updatedAt = Instant.now();
    }

    // Constructor protegido para JPA
    protected ProductEntity() {}

    // Constructor de dominio — package-private
    ProductEntity(String code, String name, String description,
                  String imageUrl, BigDecimal price, String category) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
    }

    // Getters y setters package-private
    Long getId() { return id; }
    String getCode() { return code; }
    String getName() { return name; }
    String getDescription() { return description; }
    String getImageUrl() { return imageUrl; }
    BigDecimal getPrice() { return price; }
    String getCategory() { return category; }

    void setName(String name) { this.name = name; }
    void setDescription(String description) { this.description = description; }
    void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    void setPrice(BigDecimal price) { this.price = price; }
    void setCategory(String category) { this.category = category; }
}