package com.ecmsp.orderservice.order.config;

import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.reservation.ReservationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class OrderConfiguration {

    @Bean
    OrderReturnabilityService orderReturnService(OrderRepository orderRepository) {
        return new OrderReturnabilityService(orderRepository);
    }

    @Bean
    OrderMapper orderMapper() {
        return new OrderMapper();
    }


    @Bean
    OrderFacade orderFacade(
        OrderRepository orderRepository,
        OrderIdGenerator orderIdGenerator,
        OrderEventPublisher orderEventPublisher,
        OrderReturnabilityService orderReturnabilityService,
        ReservationClient reservationClient,
        OrderMapper orderMapper,
        Clock clock
    ) {
        return new DefaultOrderFacade(
            /* orderRepository = */ orderRepository,
            /* orderIdGenerator = */ orderIdGenerator,
            /* orderEventPublisher = */ orderEventPublisher,
            /* orderReturnService = */ orderReturnabilityService,
            /* reservationClient = */ reservationClient,
            /* orderMapper = */ orderMapper,
            /* clock = */ clock
        );
    }


}
