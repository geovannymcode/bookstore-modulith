package com.geovannycode.bookstore.catalog.command;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String code) {
        super("Ya existe un producto con código: " + code);
    }
}
