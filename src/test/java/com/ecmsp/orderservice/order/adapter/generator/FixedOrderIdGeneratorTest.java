package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class FixedOrderIdGeneratorTest {

    private static final CorrelationId FIXED_CORRELATION_ID = new CorrelationId(UUID.fromString("456789ab-cdef-1234-5678-90abcdef1234"));

    @Test
    void should_generate_fixed_order_id_successfully_when_correlation_id_does_not_exist(){
        // given:
        OrderIdMappingService mappingService = new OrderIdMappingService();
        OrderIdMappingService spyMappingService = spy(mappingService);
        OrderIdGenerator fixedOrderIdGenerator = new FixedOrderIdGenerator(spyMappingService);

        // when:
        OrderId orderId = fixedOrderIdGenerator.generate(FIXED_CORRELATION_ID);

        // then:
        // Verify method calls
        verify(spyMappingService).getOrderIdByCorrelation(FIXED_CORRELATION_ID);
        verify(spyMappingService).mapCorrelationToOrder(FIXED_CORRELATION_ID, orderId);

        // Verify actual results
        assertThat(spyMappingService.getOrderIdByCorrelation(FIXED_CORRELATION_ID)).isPresent();
        assertThat(orderId).isEqualTo(new OrderId(FIXED_CORRELATION_ID.value()));
    }


    @Test
    void should_generate_fixed_order_id_successfully_when_correlation_id_exists(){
        // given:
        // Pre-register an OrderId with the same value as FIXED_CORRELATION_ID
        OrderId existingOrderId = new OrderId(FIXED_CORRELATION_ID.value());

        OrderIdMappingService mappingService = new OrderIdMappingService();
        mappingService.mapCorrelationToOrder(FIXED_CORRELATION_ID, existingOrderId);

        OrderIdMappingService spyMappingService = spy(mappingService);
        OrderIdGenerator fixedOrderIdGenerator = new FixedOrderIdGenerator(spyMappingService);

        // when:
        OrderId orderId = fixedOrderIdGenerator.generate(FIXED_CORRELATION_ID);

        // then:
        // Verify method calls
        verify(spyMappingService).getOrderIdByCorrelation(FIXED_CORRELATION_ID);
        verify(spyMappingService, never()).mapCorrelationToOrder(eq(FIXED_CORRELATION_ID), any(OrderId.class));


        // Verify actual results
        assertThat(spyMappingService.getOrderIdByCorrelation(FIXED_CORRELATION_ID)).isPresent();
        assertThat(orderId).isEqualTo(existingOrderId);

    }


    @Test
    void should_generate_random_order_id_when_correlation_id_is_null() {
        // given:
        OrderIdMappingService mappingService = new OrderIdMappingService();
        OrderIdMappingService spyMappingService = spy(mappingService);
        OrderIdGenerator fixedOrderIdGenerator = new FixedOrderIdGenerator(spyMappingService);

        // when:
        OrderId orderId = fixedOrderIdGenerator.generate(null);

        // then:
        // Verify that a correlation ID was created and used
        verify(spyMappingService).getOrderIdByCorrelation(any(CorrelationId.class));
        verify(spyMappingService).mapCorrelationToOrder(any(CorrelationId.class), eq(orderId));

        // Verify that an OrderId was generated
        assertThat(orderId).isNotNull();
        assertThat(orderId.value()).isNotNull();
    }
}
