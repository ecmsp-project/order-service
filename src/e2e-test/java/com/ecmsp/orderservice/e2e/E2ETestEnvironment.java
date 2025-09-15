package com.ecmsp.orderservice.e2e;

import com.ecmsp.orderservice.OrderServiceApplication;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;

import java.net.URI;

public final class E2ETestEnvironment {
    private static final Network NETWORK = Network.newNetwork();


    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
        new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("orderdb")
            .withUsername("admin")
            .withPassword("admin")
            .withCreateContainerCmdModifier(cmd ->
                cmd.withName("postgres")
            )
            .withNetwork(NETWORK);

    private static final KafkaContainer KAFKA_CONTAINER =
        new KafkaContainer("apache/kafka:3.7.0")
            .withNetwork(NETWORK)
            .withExposedPorts(9092, 9093)
            .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://kafka:9092")
            .withCreateContainerCmdModifier(cmd ->
                cmd.withName("kafka")
            );

    static {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (KAFKA_CONTAINER.isCreated()) {
                KAFKA_CONTAINER.stop();
            }
            if (POSTGRES_CONTAINER.isCreated()) {
                POSTGRES_CONTAINER.stop();
            }
            NETWORK.close();
        }));

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.testcontainers.debug", "true");
        POSTGRES_CONTAINER.start();
        KAFKA_CONTAINER.start();
        try {
            String[] args = new String[] {
                "--spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                "--spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                "--spring.datasource.password=" + POSTGRES_CONTAINER.getPassword(),
                "--spring.kafka.bootstrapServers=" + KAFKA_CONTAINER.getBootstrapServers(),
                "--order.id-generator.type=fixed"
            };
            SpringApplication.run(OrderServiceApplication.class, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String kafkaBootstrapServers() {
        return KAFKA_CONTAINER.getBootstrapServers();
    }

    public static URI orderServiceGrpcUrl() {
        return URI.create("http://localhost:9090");
    }

    public static URI orderServiceRestUrl() {
        return URI.create("http://localhost:8080");
    }
}
