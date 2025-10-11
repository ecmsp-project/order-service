package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.format.DateTimeFormatter;

class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    private final KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> statusUpdatedKafkaTemplate;


    private final KafkaTemplate<String, KafkaOrderCreatedEvent> orderCreatedKafkaTemplate;
    

    @Value("${kafka.topic.order-status-updated}")
    private String orderStatusUpdatedTopic;

    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    public KafkaOrderEventPublisher(KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> statusUpdatedKafkaTemplate, KafkaTemplate<String, KafkaOrderCreatedEvent> orderCreatedKafkaTemplate) {
        this.statusUpdatedKafkaTemplate = statusUpdatedKafkaTemplate;
        this.orderCreatedKafkaTemplate = orderCreatedKafkaTemplate;
    }

    @Override
    public void publish(OrderEvent event) {

        switch (event){
            case OrderEvent.OrderStatusUpdated orderStatusUpdatedEvent -> {
                KafkaOrderStatusUpdatedEvent kafkaEvent = new KafkaOrderStatusUpdatedEvent(
                        orderStatusUpdatedEvent.orderId().value().toString(),
                        orderStatusUpdatedEvent.status().name()
                );
                statusUpdatedKafkaTemplate.send(orderStatusUpdatedTopic, kafkaEvent.orderId(), kafkaEvent);
            }
            case OrderEvent.OrderCreated orderCreatedEvent -> {
                KafkaOrderCreatedEvent kafkaEvent = new KafkaOrderCreatedEvent(
                        orderCreatedEvent.orderId().value().toString(),
                        orderCreatedEvent.clientId().value().toString(),
                        orderCreatedEvent.orderTotal(),
                        orderCreatedEvent.requestedAt().format(DATE_FORMATTER)
                );
                orderCreatedKafkaTemplate.send(orderCreatedTopic, kafkaEvent.orderId(), kafkaEvent);
            }
        }



    }
}

