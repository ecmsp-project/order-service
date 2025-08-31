package com.ecmsp.orderservice.order.domain;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    PAID,
    FAILED,
    CANCELLED,
    UNSPECIFIED // Added to match the protobuf enum
}