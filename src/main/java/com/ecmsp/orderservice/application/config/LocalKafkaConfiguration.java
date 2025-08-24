package com.ecmsp.orderservice.application.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.KafkaContainer;

@Configuration
@Profile("local")
class LocalKafkaConfiguration {

    @Bean
    @Qualifier("kafka-network")
    Network kafkaNetwork() {
        return Network.newNetwork();
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer(@Qualifier("kafka-network") Network kafkaNetwork) {
        return new KafkaContainer("apache/kafka:3.7.0")
            .withNetwork(kafkaNetwork)
            .withExposedPorts(9092, 9093)
            .withCreateContainerCmdModifier(cmd ->
                cmd
                    .withName("kafka-broker")
                    .getHostConfig().withPortBindings(
                        // set fixed port on localhost to 9092
                        new PortBinding(
                            /* hostPort = */ Ports.Binding.bindPort(9092),
                            /* containerPort = */ new ExposedPort(9092)
                        ),
                        new PortBinding(
                            /* hostPort = */ Ports.Binding.bindPort(9093),
                            /* containerPort = */ new ExposedPort(9093)
                        )
                    )
            );
    }

    @Bean
    GenericContainer<?> kafkaUiContainer(
        @Qualifier("kafka-network") Network kafkaNetwork,
        KafkaContainer kafkaContainer
    ) {
        var container = new GenericContainer<>("provectuslabs/kafka-ui:latest")
            .withNetwork(kafkaNetwork)
            .dependsOn(kafkaContainer)
            .withExposedPorts(8080)
            // set fixed port on localhost to 8088
            .withCreateContainerCmdModifier(cmd ->
                cmd
                    .withName("kafka-ui")
                    .getHostConfig().withPortBindings(new PortBinding(
                        /* hostPort = */ Ports.Binding.bindPort(8088),
                        /* containerPort = */ new ExposedPort(8080)
                    ))
            )
            .withEnv("KAFKA_CLUSTERS_0_NAME", "local")
            .withEnv("KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS", "kafka-broker:9093")
            .withEnv("KAFKA_CLUSTERS_0_READONLY", "false")
            .waitingFor(Wait.forListeningPort());

        container.start();
        return container;
    }

}
