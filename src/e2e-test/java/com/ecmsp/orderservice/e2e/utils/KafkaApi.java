package com.ecmsp.orderservice.e2e.utils;

import com.ecmsp.orderservice.api.kafka.CartCreatedEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.UUID;

public class KafkaApi {


    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;


    public KafkaApi(URI uri) {
        this.objectMapper = new ObjectMapper();
        this.producer = createProducer(uri);
    }

    private KafkaProducer<String, String> createProducer(URI uri) {
        var props = new java.util.Properties();
        props.put("bootstrap.servers", uri.getHost() + ":" + uri.getPort());
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }


    public void sendEvent(CartCreatedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            sendEvent("cart-event", String.valueOf(UUID.randomUUID()), eventJson);
            System.out.println("Sent CartCreatedEvent: " + eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendEvent(String topic, String key, String value) {
        var record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
        producer.flush();
    }




}
