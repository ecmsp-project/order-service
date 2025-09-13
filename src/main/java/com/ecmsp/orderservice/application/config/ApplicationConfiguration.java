package com.ecmsp.orderservice.application.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    Flyway flyway() {
        return Flyway.configure()
            .baselineOnMigrate(true)
            .load();
    }


}
