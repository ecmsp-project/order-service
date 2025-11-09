package com.ecmsp.orderservice.order.domain;

import com.ecmsp.orderservice.order.domain.reservation.ReservationCreated;

public record OrderCreated(
        Boolean isCreatedSuccessfully,
        OrderId orderId,
        ReservationCreated reservationCreated
) {
}
