package com.ecmsp.orderservice.order.domain;

import com.ecmsp.orderservice.order.domain.reservation.ReservationId;

import java.util.List;

//TODO: remove reservationId everywhere
public record OrderToCreate(
    ReservationId reservationId,
    ClientId clientId,
    List<OrderItem> items
) {
}
