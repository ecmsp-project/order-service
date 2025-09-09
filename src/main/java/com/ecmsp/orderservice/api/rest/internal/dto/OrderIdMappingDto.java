package com.ecmsp.orderservice.api.rest.internal.dto;

import java.util.UUID;

public record OrderIdMappingDto(
        UUID correlationId, UUID orderId) {
}
