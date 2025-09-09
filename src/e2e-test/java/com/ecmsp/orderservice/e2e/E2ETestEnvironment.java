package com.ecmsp.orderservice.e2e;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.kafka.KafkaContainer;

import java.net.URI;
import java.nio.file.Paths;

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
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.withName("kafka")
                    );

    private static final GenericContainer<?> ORDER_SERVICE_CONTAINER =
            new GenericContainer<>(
                    new ImageFromDockerfile()
                            .withFileFromPath("Dockerfile", Paths.get("Dockerfile"))
                            .withFileFromPath("target/app.jar", Paths.get("target/app.jar"))
            )
                    .withExposedPorts(8080)
                    .waitingFor(
                            Wait.forHttp("/health")
                                    .forStatusCode(200)
                                    .withStartupTimeout(java.time.Duration.ofSeconds(20))
                    )
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.withName("order-service")
                    )
                    .withNetwork(NETWORK)
                    .dependsOn(KAFKA_CONTAINER)
                    .withEnv("DB_URL", "jdbc:postgresql://postgres:5432/orderdb")
                    .withEnv("DB_USERNAME", "admin")
                    .withEnv("DB_PASSWORD", "admin")
                    .withEnv("KAFKA_URL", "kafka:9094");

    static {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(ORDER_SERVICE_CONTAINER.isCreated()){
                ORDER_SERVICE_CONTAINER.stop();
            }
            if(KAFKA_CONTAINER.isCreated()){
                KAFKA_CONTAINER.stop();
            }
            if(POSTGRES_CONTAINER.isCreated()){
                POSTGRES_CONTAINER.stop();
            }
            NETWORK.close();
        }));

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.testcontainers.debug", "true");
        POSTGRES_CONTAINER.start();
        KAFKA_CONTAINER.start();
        try {
            ORDER_SERVICE_CONTAINER.start(); // start only once
        } catch (Exception e) {
            System.out.println("Container logs: \n" + ORDER_SERVICE_CONTAINER.getLogs());
            throw new RuntimeException(e);
        }

    }

    public static URI getUrl(Containers container) {
        return switch (container) {
            case ORDER_SERVICE ->
                    URI.create("http://" + ORDER_SERVICE_CONTAINER.getHost() + ":" + ORDER_SERVICE_CONTAINER.getMappedPort(8080));
            case KAFKA -> URI.create("http://" + KAFKA_CONTAINER.getHost() + ":" + KAFKA_CONTAINER.getMappedPort(9093));
            case POSTGRES ->
                    URI.create("http://" + POSTGRES_CONTAINER.getHost() + ":" + POSTGRES_CONTAINER.getMappedPort(5432));
        };
    }



    public enum Containers {
        ORDER_SERVICE, KAFKA, POSTGRES
    }
}
