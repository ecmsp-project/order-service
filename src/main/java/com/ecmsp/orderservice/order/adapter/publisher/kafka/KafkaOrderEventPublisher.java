package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.format.DateTimeFormatter;

class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    //TODO only orderCreatedEvent is needed there won't be sending any statusUpdatedEvent so it should be removed
    private final KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> statusUpdatedKafkaTemplate;


    private final KafkaTemplate<String, KafkaOrderCreatedEvent> orderCreatedKafkaTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    

    @Value("${kafka.topic.order-status-updated}")
    private String orderStatusUpdatedTopic;

    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    public KafkaOrderEventPublisher(KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> statusUpdatedKafkaTemplate, KafkaTemplate<String, KafkaOrderCreatedEvent> orderCreatedKafkaTemplate, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.statusUpdatedKafkaTemplate = statusUpdatedKafkaTemplate;
        this.orderCreatedKafkaTemplate = orderCreatedKafkaTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderEvent event) {

        switch (event){
            case OrderEvent.OrderStatusUpdated orderStatusUpdatedEvent -> {
                KafkaOrderStatusUpdatedEvent kafkaEvent = new KafkaOrderStatusUpdatedEvent(
                        orderStatusUpdatedEvent.orderId().value().toString(),
                        orderStatusUpdatedEvent.status().name()
                );
                sendEvent(orderStatusUpdatedTopic, kafkaEvent.orderId(), kafkaEvent);
            }
            case OrderEvent.OrderCreated orderCreatedEvent -> {
                KafkaOrderCreatedEvent kafkaEvent = new KafkaOrderCreatedEvent(
                        orderCreatedEvent.orderId().value().toString(),
                        orderCreatedEvent.clientId().value().toString(),
                        orderCreatedEvent.orderTotal(),
                        orderCreatedEvent.requestedAt().format(DATE_FORMATTER)
                );
                sendEvent(orderCreatedTopic, kafkaEvent.orderId(), kafkaEvent);
            }
        }
    }

    private void sendEvent(String topic, String key, Object event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, eventJson);
        } catch (JsonProcessingException e) {
            // TODO: handle error
        }
    }
}

