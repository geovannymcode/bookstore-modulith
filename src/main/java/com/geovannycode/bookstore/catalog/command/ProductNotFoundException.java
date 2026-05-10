package com.geovannycode.bookstore.catalog.command;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String code) {
        super("Producto no encontrado con código: " + code);
    }
}
