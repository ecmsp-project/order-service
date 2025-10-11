package com.ecmsp.orderservice.order.config;

import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.returns.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ReturnConfiguration {

    @Bean
    ReturnFacade returnFacade(
        ReturnRepository returnRepository,
        OrderRepository orderRepository,
        OrderReturnabilityService orderReturnabilityService,
        ReturnIdGenerator returnIdGenerator,
        Clock clock
    ) {
        return new DefaultReturnFacade(
            /* returnRepository = */ returnRepository,
            /* orderRepository = */ orderRepository,
            /* orderReturnabilityService = */ orderReturnabilityService,
            /* returnIdGenerator = */ returnIdGenerator,
            /* clock = */ clock
        );
    }
}
