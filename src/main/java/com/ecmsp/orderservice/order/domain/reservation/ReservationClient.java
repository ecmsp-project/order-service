package com.ecmsp.orderservice.order.domain.reservation;

public interface ReservationClient {
    ReservationCreated createReservation(ReservationToCreate reservationToCreate);
}
