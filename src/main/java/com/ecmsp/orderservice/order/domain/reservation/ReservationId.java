package com.ecmsp.orderservice.order.domain.reservation;

import java.util.UUID;


//TODO: should be removed (it's product service internal only)
public record ReservationId(UUID value) {

    @Override
    public String toString(){
        return value.toString();
    }

}
