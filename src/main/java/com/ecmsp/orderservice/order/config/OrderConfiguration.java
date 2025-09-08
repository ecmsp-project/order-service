package com.ecmsp.orderservice.order.config;

import com.ecmsp.orderservice.order.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class OrderConfiguration {

    @Bean
    OrderFacade orderFacade(
        OrderRepository orderRepository,
        OrderIdGenerator orderIdGenerator,
        PaymentClient paymentEventPublisher,
        OrderEventPublisher orderEventPublisher,
        Clock clock
    ) {
        return new DefaultOrderFacade(
            /* orderRepository = */ orderRepository,
            /* orderIdGenerator = */ orderIdGenerator,
            /* paymentEventPublisher = */ paymentEventPublisher,
            /* orderEventPublisher = */ orderEventPublisher,
            /* clock = */ clock
        );
    }


}
