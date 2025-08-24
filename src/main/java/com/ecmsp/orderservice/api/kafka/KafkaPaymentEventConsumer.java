package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.OrderFacade;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderStatus;
import com.ecmsp.orderservice.order.domain.OrderToUpdate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class KafkaPaymentEventConsumer {

    private final OrderFacade orderFacade;


    public KafkaPaymentEventConsumer(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }


    @KafkaListener(topics = "${kafka.topic.payment-processed-succeeded}")
    public void consume(@Payload PaymentProcessedKafkaEventSucceeded paymentProcessedKafkaEventSucceeded) {
        orderFacade.updateOrder(
                new OrderToUpdate(
                        OrderId.from(paymentProcessedKafkaEventSucceeded.orderId()),
                        OrderStatus.PAID
                )
        );
    }


    @KafkaListener(topics = "${kafka.topic.payment-processed-failed}")
    public void consume(PaymentProcessedKafkaEventFailed paymentProcessedKafkaEventFailed) {
        orderFacade.updateOrder(
                new OrderToUpdate(
                        OrderId.from(paymentProcessedKafkaEventFailed.orderId()),
                        OrderStatus.FAILED
                )
        );
    }





}
