package com.ecmsp.orderservice.order.domain;

import java.util.List;


public record OrderToCreate(
    ReservationId reservationId,
    ClientId clientId,
    List<OrderItem> items
) {
}
