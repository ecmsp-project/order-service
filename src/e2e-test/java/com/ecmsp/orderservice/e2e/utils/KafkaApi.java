package com.ecmsp.orderservice.e2e.utils;

import com.ecmsp.orderservice.api.kafka.KafkaCartCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.List;
import java.util.UUID;

public class KafkaApi {


    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;


    public KafkaApi(String bootstrapServers) {
        this.objectMapper = new ObjectMapper();
        this.producer = createProducer(bootstrapServers);
    }

    private KafkaProducer<String, String> createProducer(String bootstrapServers) {
        var props = new java.util.Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }


    public void sendEvent(KafkaCartCreatedEvent event, String correlationId) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            sendEvent("cart-event", String.valueOf(UUID.randomUUID()), eventJson, List.of(
                    new RecordHeader("__TypeId__", KafkaCartCreatedEvent.class.getCanonicalName().getBytes()),
                    new RecordHeader("X-Correlation-Id", correlationId.getBytes())
            ));
            System.out.println("Sent CartCreatedEvent: " + eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }



    private void sendEvent(String topic, String key, String value, List<Header> headers) {
        var record = new ProducerRecord<>(topic, null, key, value, headers);
        producer.send(record);
        producer.flush();
    }




}
