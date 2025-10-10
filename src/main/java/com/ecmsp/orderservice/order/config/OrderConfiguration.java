package com.ecmsp.orderservice.order.config;

import com.ecmsp.orderservice.order.domain.*;
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
    OrderFacade orderFacade(
        OrderRepository orderRepository,
        OrderIdGenerator orderIdGenerator,
        OrderEventPublisher orderEventPublisher,
        OrderReturnabilityService orderReturnabilityService,
        Clock clock
    ) {
        return new DefaultOrderFacade(
            /* orderRepository = */ orderRepository,
            /* orderIdGenerator = */ orderIdGenerator,
            /* orderEventPublisher = */ orderEventPublisher,
            /* orderReturnService = */ orderReturnabilityService,
            /* clock = */ clock
        );
    }


}
