package com.ecmsp.orderservice.order.api;

import com.ecmsp.orderservice.order.domain.OrderFacade;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class OrdersControllerTestConfiguration {

    @Bean
    OrderFacade orderFacade() {
        return mock(OrderFacade.class);
    }

}
