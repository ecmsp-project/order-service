package com.ecmsp.orderservice.order.adapter.repository.db;

import com.ecmsp.orderservice.order.domain.OrderRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(
    prefix = "order.repository",
    name = "type",
    havingValue = "db")
@Import({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.ecmsp.orderservice.order.adapter.repository.db")
class DbOrderRepositoryConfiguration {

    @Bean
    OrderRepository dbOrderRepository(OrderEntityRepository orderEntityRepository) {
        return new DbOrderRepository(
            /* orderEntityRepository = */ orderEntityRepository
        );
    }

}
