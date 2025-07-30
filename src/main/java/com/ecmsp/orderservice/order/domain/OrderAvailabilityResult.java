package com.ecmsp.orderservice.order.domain;

import java.util.Map;

public record OrderAvailabilityResult(
        boolean available,
        String message,
        Map<String, ItemId> unavailableItems
        ) {

        @Override
        public String toString() {
            return "OrderAvailabilityResult{" +
                    "available=" + available +
                    ", message='" + message + '\'' +
                    ", unavailableItems=" + unavailableItemsAsString() +
                    '}';
        }


        public String unavailableItemsAsString() {
            return unavailableItems.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue().value())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("No unavailable items");
        }


}
