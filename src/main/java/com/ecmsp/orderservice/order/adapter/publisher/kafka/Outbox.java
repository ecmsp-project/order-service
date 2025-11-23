package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@Slf4j
class Outbox {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final KafkaEventEntityRepository kafkaEventEntityRepository;
    private final ObjectMapper objectMapper;

    private final String orderStatusUpdatedTopic;
    private final String orderCreatedTopic;

    public Outbox(
        KafkaEventEntityRepository kafkaEventEntityRepository,
        ObjectMapper objectMapper,
        String orderStatusUpdatedTopic,
        String orderCreatedTopic
    ) {
        this.kafkaEventEntityRepository = kafkaEventEntityRepository;
        this.objectMapper = objectMapper;
        this.orderStatusUpdatedTopic = orderStatusUpdatedTopic;
        this.orderCreatedTopic = orderCreatedTopic;
    }

    public void publish(OrderEvent event) {
        try {
            doPublish(event);
        } catch (Exception e) {
            log.error("Failed to publish event to outbox.", e);
        }

    }

    private void doPublish(OrderEvent event) throws Exception {
        var eventToSave = switch (event) {
            case OrderEvent.OrderStatusUpdated orderStatusUpdatedEvent -> {
                KafkaOrderStatusUpdatedEvent kafkaEvent = new KafkaOrderStatusUpdatedEvent(
                    orderStatusUpdatedEvent.orderId().value().toString(),
                    orderStatusUpdatedEvent.status().name()
                );

                yield KafkaEventEntity.builder()
                    .topic(orderStatusUpdatedTopic)
                    .key(kafkaEvent.orderId())
                    .payload(objectMapper.writeValueAsString(kafkaEvent))
                    .build();
            }
            case OrderEvent.OrderCreated orderCreatedEvent -> {
                KafkaOrderCreatedEvent kafkaEvent = new KafkaOrderCreatedEvent(
                    orderCreatedEvent.orderId().value().toString(),
                    orderCreatedEvent.clientId().value().toString(),
                    orderCreatedEvent.orderTotal(),
                    orderCreatedEvent.requestedAt().format(DATE_FORMATTER)
                );

                yield KafkaEventEntity.builder()
                    .topic(orderCreatedTopic)
                    .key(kafkaEvent.orderId())
                    .payload(objectMapper.writeValueAsString(kafkaEvent))
                    .build();
            }
        };

        kafkaEventEntityRepository.save(eventToSave);
    }

}
