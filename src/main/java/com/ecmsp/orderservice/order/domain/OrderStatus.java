package com.ecmsp.orderservice.order.domain;

public enum OrderStatus {
    UNSPECIFIED,
    PENDING,
    PROCESSING,
    PAID,
    FAILED,
    CANCELLED,

    // Post-fulfillment states
    SHIPPED,
    DELIVERED,

    // Return states
    RETURN_REQUESTED,
    RETURN_PROCESSING,
    RETURNED
}