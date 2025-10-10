
package com.ecmsp.orderservice.application.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("dev")
public class DevPostgreSQLConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("order-service-db")
                .withExposedPorts(5432)
                .withCreateContainerCmdModifier(cmd ->
                        cmd
                                .withName("postgres-db")
                                .getHostConfig().withPortBindings(
                                        new PortBinding(
                                                /* hostPort = */ Ports.Binding.bindPort(9300),
                                                /* containerPort = */ new ExposedPort(5432)
                                        )
                                )
                )
                .withUsername("admin")
                .withPassword("admin");
    }

}