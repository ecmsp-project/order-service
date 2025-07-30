package com.ecmsp.orderservice.order.adapter.payment.kafka;

import com.ecmsp.orderservice.order.domain.PaymentClient;
import com.ecmsp.orderservice.order.domain.PaymentToCreate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;



public class KafkaPaymentClient implements PaymentClient {


    private KafkaTemplate<String, PaymentRequestedKafkaEvent> kafkaTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Value("${kafka.topic.payment-request}")
    private String paymentRequestTopic;

    public KafkaPaymentClient(KafkaTemplate<String, PaymentRequestedKafkaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void createPayment(PaymentToCreate paymentToCreate) {

        PaymentRequestedKafkaEvent paymentEvent = new PaymentRequestedKafkaEvent(
                paymentToCreate.orderId().value().toString(),
                paymentToCreate.clientId().value().toString(),
                paymentToCreate.amount(),
                paymentToCreate.requestedAt().format(DATE_FORMATTER)
        );
        kafkaTemplate.send(paymentRequestTopic, paymentEvent.orderId(), paymentEvent);
    }
}
