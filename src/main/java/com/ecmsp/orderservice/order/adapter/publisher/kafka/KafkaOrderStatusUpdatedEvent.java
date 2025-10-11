package com.ecmsp.orderservice.order.adapter.publisher.kafka;

record KafkaOrderStatusUpdatedEvent(
        String orderId,
        String status
) {

}
