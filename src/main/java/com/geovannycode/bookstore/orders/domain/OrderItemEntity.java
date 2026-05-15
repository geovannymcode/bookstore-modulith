package com.geovannycode.bookstore.orders.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Representa un ítem dentro de una orden.
 *
 * Cada orden puede tener múltiples ítems, cada uno con su producto,
 * precio (snapshot al momento de la compra) y cantidad.
 */
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_id_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private int quantity;

    protected OrderItemEntity() {}

    public OrderItemEntity(String productCode, String productName, BigDecimal productPrice, int quantity) {
        this.productCode = productCode;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
