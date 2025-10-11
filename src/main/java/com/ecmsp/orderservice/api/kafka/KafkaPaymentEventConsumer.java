package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.OrderFacade;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderStatus;
import com.ecmsp.orderservice.order.domain.OrderToUpdate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
class KafkaPaymentEventConsumer {

    private final OrderFacade orderFacade;


    public KafkaPaymentEventConsumer(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }


    @KafkaListener(topics = "${kafka.topic.payment-processed-succeeded}")
    public void consume(@Payload KafkaPaymentProcessedSucceededEvent paymentProcessedKafkaEventSucceeded) {
        orderFacade.updateOrder(
                new OrderToUpdate(
                        new OrderId(UUID.fromString(paymentProcessedKafkaEventSucceeded.orderId())),
                        OrderStatus.PAID
                )
        );
    }


    @KafkaListener(topics = "${kafka.topic.payment-processed-failed}")
    public void consume(KafkaPaymentProcessedFailedEvent paymentProcessedKafkaEventFailed) {
        orderFacade.updateOrder(
                new OrderToUpdate(
                        new OrderId(UUID.fromString(paymentProcessedKafkaEventFailed.orderId())),
                        OrderStatus.FAILED
                )
        );
    }





}
