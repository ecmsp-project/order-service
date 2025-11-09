package com.ecmsp.orderservice.order.adapter.reservation.grpc;

import com.ecmsp.orderservice.order.domain.reservation.ReservationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
class ReservationGrpcClientConfiguration {

    @Bean
    ReservationClient reservationGrpcClient(ReservationGrpcMapper reservationGrpcMapper) {
        return new ReservationGrpcClient(reservationGrpcMapper);
    }
}
