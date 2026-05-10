package com.geovannycode.bookstore.catalog.web;

import com.geovannycode.bookstore.catalog.command.ProductNotFoundException;
import com.geovannycode.bookstore.catalog.command.ProductAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.geovannycode.bookstore.catalog")
class CatalogExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handle(ProductNotFoundException ex) {
        var problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Producto no encontrado");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handle(ProductAlreadyExistsException ex) {
        var problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Producto ya existe");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
