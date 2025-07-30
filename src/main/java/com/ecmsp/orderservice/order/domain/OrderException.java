package com.ecmsp.orderservice.order.domain;

import java.util.Map;

public abstract class OrderException extends RuntimeException {

    protected OrderException(String message) {
        super(message);
    }

    public static class NotFound extends OrderException {

        public NotFound(OrderId orderId) {
            super("Order with id `%s` not found.".formatted(orderId.value()));
        }
    }

    public static class AlreadyExists extends OrderException {

        public AlreadyExists(OrderId orderId) {
            super("Order with id `%s` already exists.".formatted(orderId.value()));
        }
    }


    public static class ItemsNotAvailable extends OrderException {
        public ItemsNotAvailable(String message, String unavailableItems) {
            super("Items not available: %s. %s".formatted(unavailableItems, message));
        }
    }

}
