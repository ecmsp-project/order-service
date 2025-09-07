package com.ecmsp.orderservice.order.config;

import com.ecmsp.orderservice.order.adapter.generator.GeneratorFacade;
import com.ecmsp.orderservice.order.domain.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class OrderConfiguration {

    private final GeneratorFacade generatorFacade;

    OrderConfiguration(GeneratorFacade generatorFacade) {
        this.generatorFacade = generatorFacade;
    }


    @Bean
    @ConditionalOnProperty(
            name = "order.id-generator.type",
            havingValue = "random",
            matchIfMissing = true  // Default to random
    )
    OrderIdGenerator randomOrderIdGenerator(){
        return generatorFacade.getRandomOrderIdGenerator();
    }

    @Bean
    @ConditionalOnProperty(
            name = "order.id-generator.type",
            havingValue = "fixed",
            matchIfMissing = false
    )
    OrderIdGenerator fixedOrderIdGenerator(){
        return generatorFacade.getFixedOrderIdGenerator();
    }



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
