package com.ecmsp.orderservice.order.config;

import com.ecmsp.orderservice.order.domain.VariantId;
import com.ecmsp.orderservice.order.domain.reservation.ReservationClient;
import com.ecmsp.orderservice.order.domain.reservation.ReservationCreated;
import com.ecmsp.orderservice.order.domain.reservation.ReservationToCreate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Collections;
import java.util.List;

@TestConfiguration
public class TestReservationClientConfiguration {

    @Bean
    @Primary
    public ReservationClient testReservationClient() {
        return new TestReservationClient();
    }

    private static class TestReservationClient implements ReservationClient {
        @Override
        public ReservationCreated createReservation(ReservationToCreate reservationToCreate) {
            // Return successful reservation with all requested variant IDs
            List<VariantId> reservedVariantIds = reservationToCreate.variantsToReserve().stream()
                    .map(ReservationToCreate.VariantToReserve::variantId)
                    .toList();

            return new ReservationCreated(reservedVariantIds, Collections.emptyList());
        }
    }
}
