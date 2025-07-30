package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaOrderEventPublisher implements OrderEventPublisher {
    private KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> kafkaTemplate;

    @Value("${kafka.topic.order-status-updated}")
    private String orderStatusUpdatedTopic;

    public KafkaOrderEventPublisher(KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publish(OrderEvent event) {

        switch (event){
            case OrderEvent.OrderStatusUpdated orderStatusUpdatedEvent -> {
                KafkaOrderStatusUpdatedEvent kafkaEvent = new KafkaOrderStatusUpdatedEvent(
                        orderStatusUpdatedEvent.orderId().value().toString(),
                        orderStatusUpdatedEvent.status().name()
                );
                kafkaTemplate.send(orderStatusUpdatedTopic, kafkaEvent.orderId(), kafkaEvent);
            }
        }



    }
}

