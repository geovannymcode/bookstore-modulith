package com.geovannycode.bookstore.config;

import com.geovannycode.bookstore.exceptions.InvalidOrderException;
import com.geovannycode.bookstore.exceptions.OrderNotFoundException;
import com.geovannycode.bookstore.exceptions.ProductAlreadyExistsException;
import com.geovannycode.bookstore.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

/**
 * Manejador global de excepciones.
 *
 * ⚠️ PROBLEMA (para el workshop): esta clase conoce las excepciones de
 * TODOS los dominios (Catalog, Orders, futuros módulos). Cada vez que
 * se agrega un dominio nuevo, hay que modificar este archivo.
 *
 * Consecuencias reales:
 * - Conflictos de Git cuando dos features tocan distintos dominios
 * - Un solo desarrollador como "dueño" de este archivo
 * - Conocimiento acoplado: si extraes Catalog como microservicio,
 *   ProductNotFoundException ya no debería vivir aquí
 *
 * Objetivo: dividirlo en CatalogExceptionHandler y OrdersExceptionHandler,
 * cada uno en su propio módulo con @RestControllerAdvice(basePackages=...)
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ── Catalog ───────────────────────────────────────────────────────

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handle(ProductNotFoundException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Producto no encontrado");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handle(ProductAlreadyExistsException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Producto ya existe");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // ── Orders ────────────────────────────────────────────────────────

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handle(OrderNotFoundException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Orden no encontrada");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidOrderException.class)
    ProblemDetail handle(InvalidOrderException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Orden inválida");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
