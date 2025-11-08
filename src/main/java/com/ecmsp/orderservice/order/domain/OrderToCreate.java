package com.ecmsp.orderservice.order.domain;

import java.util.List;

//TODO: remove reservationId everywhere
public record OrderToCreate(
    ReservationId reservationId,
    ClientId clientId,
    List<OrderItem> items
) {
}
