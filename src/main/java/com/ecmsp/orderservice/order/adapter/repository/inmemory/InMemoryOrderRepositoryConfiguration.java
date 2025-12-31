package com.ecmsp.orderservice.order.adapter.repository.inmemory;

import com.ecmsp.orderservice.order.domain.OrderRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "order.repository",
    name = "type",
    havingValue = "in-memory"
)
class InMemoryOrderRepositoryConfiguration {

    @Bean
    OrderRepository inMemoryOrderRepository() {
        return new InMemoryOrderRepository();
    }

}
