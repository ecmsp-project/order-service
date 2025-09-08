package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.OrderIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OrderIdGeneratorConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = "order.id-generator.type",
            havingValue = "fixed",
            matchIfMissing = false
    )
    OrderIdGenerator fixedOrderIdGenerator(OrderIdMappingService mappingService) {
        return new FixedOrderIdGenerator(mappingService);
    }

    @Bean
    @ConditionalOnProperty(
            name = "order.id-generator.type",
            havingValue = "random",
            matchIfMissing = true  // Default to random
    )
    OrderIdGenerator randomOrderIdGenerator() {
        return new RandomOrderIdGenerator();
    }
}