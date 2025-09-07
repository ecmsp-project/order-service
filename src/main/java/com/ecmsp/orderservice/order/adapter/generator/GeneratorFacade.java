package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.OrderIdGenerator;
import org.springframework.stereotype.Component;

@Component
public class GeneratorFacade {

    public static OrderIdGenerator getRandomOrderIdGenerator() {
        return new RandomOrderIdGenerator();
    }

    public static OrderIdGenerator getFixedOrderIdGenerator() {
        return new FixedOrderIdGenerator();
    }

}
