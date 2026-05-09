package com.geovannycode.bookstore.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Orden no encontrada: " + orderNumber);
    }
}
