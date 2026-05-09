package com.geovannycode.bookstore.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String code) {
        super("Ya existe un producto con código: " + code);
    }
}
