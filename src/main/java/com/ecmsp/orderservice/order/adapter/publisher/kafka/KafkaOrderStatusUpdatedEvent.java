package com.ecmsp.orderservice.order.adapter.publisher.kafka;

public record KafkaOrderStatusUpdatedEvent(
        String orderId,
        String status
) {

}
