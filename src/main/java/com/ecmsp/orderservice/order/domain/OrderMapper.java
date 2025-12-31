package com.ecmsp.orderservice.order.domain;

import com.ecmsp.orderservice.order.domain.reservation.ReservationToCreate;
import org.springframework.stereotype.Component;

public class OrderMapper {

    public ReservationToCreate toReservationToCreate(Order order) {
        return new ReservationToCreate(
            order.orderId(),
            order.items().stream()
                .map(item -> new ReservationToCreate.VariantToReserve(
                    item.variantId(),
                    item.quantity()
                ))
                .toList()
        );
    }

}
