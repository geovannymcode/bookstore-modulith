package com.geovannycode.bookstore.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String code) {
        super("Producto no encontrado con código: " + code);
    }
}
