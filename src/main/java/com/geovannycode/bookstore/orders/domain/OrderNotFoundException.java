package com.geovannycode.bookstore.orders.domain;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Orden no encontrada: " + orderNumber);
    }
}
