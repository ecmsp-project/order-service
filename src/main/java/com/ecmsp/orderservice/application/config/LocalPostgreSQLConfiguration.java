package com.ecmsp.orderservice.application.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
@Profile("local")
public class LocalPostgreSQLConfiguration {

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
                                                /* hostPort = */ Ports.Binding.bindPort(5432),
                                                /* containerPort = */ new ExposedPort(5432)
                                        )
                                )
                )
                .withUsername("admin")
                .withPassword("admin");
    }

//    @Bean
//    @DependsOn("postgresContainer")
//    public Flyway flyway(DataSource dataSource) {
//        Flyway flyway = Flyway.configure()
//                .dataSource(dataSource)
//                .locations("classpath:db/migration")
//                .baselineOnMigrate(true)
//                .load();
//        flyway.migrate();
//        return flyway;
//    }
}