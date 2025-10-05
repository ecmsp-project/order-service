package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.returns.ReturnIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ReturnIdGeneratorConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = "return.id-generator.type",
            havingValue = "fixed",
            matchIfMissing = false
    )
    ReturnIdGenerator fixedReturnIdGenerator(ReturnIdMappingService mappingService) {
        return new FixedReturnIdGenerator(mappingService);
    }

    @Bean
    @ConditionalOnProperty(
            name = "return.id-generator.type",
            havingValue = "random",
            matchIfMissing = true  // Default to random
    )
    ReturnIdGenerator randomReturnIdGenerator() {
        return new RandomReturnIdGenerator();
    }
}
