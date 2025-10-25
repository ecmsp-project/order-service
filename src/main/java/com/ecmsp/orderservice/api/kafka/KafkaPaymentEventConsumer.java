package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.OrderFacade;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderStatus;
import com.ecmsp.orderservice.order.domain.OrderToUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
class KafkaPaymentEventConsumer {

    private final OrderFacade orderFacade;
    private final ObjectMapper objectMapper;


    public KafkaPaymentEventConsumer(OrderFacade orderFacade, ObjectMapper objectMapper) {
        this.orderFacade = orderFacade;
        this.objectMapper = objectMapper;
    }


    @KafkaListener(topics = "${kafka.topic.payment-processed-succeeded}")
    public void consumeSucceeded(@Payload String paymentProcessedEventSucceeded,  @Header(value = "X-Correlation-Id", required = false) String correlationId) throws JsonProcessingException {
        log.info("Raw message received: [{}]", paymentProcessedEventSucceeded);
        log.info("Message length: {}", paymentProcessedEventSucceeded != null ? paymentProcessedEventSucceeded.length() : "null");

        KafkaPaymentProcessedSucceededEvent kafkaPaymentProcessedSucceededEvent = objectMapper.readValue(paymentProcessedEventSucceeded, KafkaPaymentProcessedSucceededEvent.class);

        handleCorrelationId(correlationId);

        orderFacade.updateOrder(
                new OrderToUpdate(
                        new OrderId(UUID.fromString(kafkaPaymentProcessedSucceededEvent.orderId())),
                        OrderStatus.PAID
                )
        );
    }


    @KafkaListener(topics = "${kafka.topic.payment-processed-failed}")
    public void consumeFailed(@Payload String paymentProcessedEventFailed, @Header(value = "X-Correlation-Id", required = false) String correlationId) throws JsonProcessingException {
        log.info("Raw message received: [{}]", paymentProcessedEventFailed);
        log.info("Message length: {}", paymentProcessedEventFailed != null ? paymentProcessedEventFailed.length() : "null");

        KafkaPaymentProcessedFailedEvent kafkaPaymentProcessedFailedEvent = objectMapper.readValue(paymentProcessedEventFailed, KafkaPaymentProcessedFailedEvent.class);

        handleCorrelationId(correlationId);

        orderFacade.updateOrder(
                new OrderToUpdate(
                        new OrderId(UUID.fromString(kafkaPaymentProcessedFailedEvent.orderId())),
                        OrderStatus.FAILED
                )
        );
    }


    private static void handleCorrelationId(String correlationId) {
        String effectiveCorrelationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        MDC.put("correlationId", effectiveCorrelationId);
        log.info("Processing orderCreated event - CorrelationID: {}", effectiveCorrelationId);
    }




}
