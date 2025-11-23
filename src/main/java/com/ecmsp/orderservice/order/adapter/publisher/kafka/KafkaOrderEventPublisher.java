package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;

class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final Outbox outbox;

    public KafkaOrderEventPublisher(Outbox outbox) {
        this.outbox = outbox;
    }

    @Override
    public void publish(OrderEvent event) {
        outbox.publish(event);
    }
}

