package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.OrderFacade;
import com.ecmsp.orderservice.order.domain.OrderToCreate;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaCartEventConsumer{

    private final OrderFacade orderFacade;

    public KafkaCartEventConsumer(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }


    @KafkaListener(topics = "${kafka.topic.cart-event}")
    public void consume(CartCreatedEvent cartEvent) {
        OrderToCreate orderToCreate = cartEvent.toOrder(cartEvent);
        orderFacade.createOrder(orderToCreate);
    }
}
