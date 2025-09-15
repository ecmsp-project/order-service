package com.ecmsp.orderservice.application.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("local")
public class LocalPostgreSQLConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("order-service-db")
                .withUsername("admin")
                .withPassword("admin");
    }
}