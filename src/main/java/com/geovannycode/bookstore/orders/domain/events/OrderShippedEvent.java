package com.geovannycode.bookstore.orders.domain.events;

public record OrderShippedEvent(String orderNumber, String trackingCode) {}