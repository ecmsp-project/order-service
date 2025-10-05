package com.ecmsp.orderservice.order.adapter.repository.inmemory;

import com.ecmsp.orderservice.order.domain.returns.ReturnRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "return.repository",
    name = "type",
    havingValue = "in-memory"
)
class InMemoryReturnRepositoryConfiguration {

    @Bean
    ReturnRepository inMemoryReturnRepository() {
        return new InMemoryReturnRepository();
    }
}
