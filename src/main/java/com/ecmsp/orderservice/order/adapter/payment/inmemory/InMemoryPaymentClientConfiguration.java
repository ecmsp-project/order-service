package com.ecmsp.orderservice.order.adapter.payment.inmemory;

import com.ecmsp.orderservice.order.domain.PaymentClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "order.payment-client",
    name = "type",
    havingValue = "in-memory")
class InMemoryPaymentClientConfiguration {

    @Bean
    PaymentClient inMemoryPaymentClient() {
        return new InMemoryPaymentClient();
    }

}
