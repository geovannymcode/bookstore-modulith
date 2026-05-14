package com.geovannycode.bookstore.orders.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_id_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private String deliveryAddress;

    /**
     * Una orden tiene múltiples ítems. Cascade ALL para que al persistir
     * la orden se persistan automáticamente los ítems.
     * orphanRemoval para que si se quita un ítem de la lista, se borre de BD.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onPrePersist() {
        this.createdAt = Instant.now();
        if (this.status == null) this.status = OrderStatus.NEW;
    }

    @PreUpdate
    void onPreUpdate() {
        this.updatedAt = Instant.now();
    }

    protected OrderEntity() {}

    public OrderEntity(String orderNumber, String customerName, String customerEmail,
                       String customerPhone, String deliveryAddress,
                       List<OrderItemEntity> items) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.items.addAll(items);
        this.status = OrderStatus.NEW;
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public List<OrderItemEntity> getItems() { return List.copyOf(items); }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setStatus(OrderStatus status) { this.status = status; }
}
