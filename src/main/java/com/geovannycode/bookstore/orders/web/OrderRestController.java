package com.geovannycode.bookstore.orders.web;


import com.geovannycode.bookstore.common.models.PagedResult;
import com.geovannycode.bookstore.orders.domain.CreateOrderRequest;
import com.geovannycode.bookstore.orders.domain.CreateOrderResponse;
import com.geovannycode.bookstore.orders.domain.OrderEntity;
import com.geovannycode.bookstore.orders.domain.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{orderNumber}")
    OrderEntity getByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber);
    }

}
