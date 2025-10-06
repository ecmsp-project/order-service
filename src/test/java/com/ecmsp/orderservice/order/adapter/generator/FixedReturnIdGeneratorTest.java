package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class FixedReturnIdGeneratorTest {

    private static final CorrelationId FIXED_CORRELATION_ID = new CorrelationId(UUID.fromString("456789ab-cdef-1234-5678-90abcdef1234"));

    @Test
    void should_generate_fixed_return_id_successfully_when_correlation_id_does_not_exist(){
        // given:
        ReturnIdMappingService mappingService = new ReturnIdMappingService();
        ReturnIdMappingService spyMappingService = spy(mappingService);
        ReturnIdGenerator fixedReturnIdGenerator = new FixedReturnIdGenerator(spyMappingService);

        // when:
        ReturnId returnId = fixedReturnIdGenerator.generate(FIXED_CORRELATION_ID);

        // then:
        // Verify method calls
        verify(spyMappingService).getReturnIdByCorrelation(FIXED_CORRELATION_ID);
        verify(spyMappingService).mapCorrelationToReturn(FIXED_CORRELATION_ID, returnId);

        // Verify actual results
        assertThat(spyMappingService.getReturnIdByCorrelation(FIXED_CORRELATION_ID)).isPresent();
        assertThat(returnId).isEqualTo(new ReturnId(FIXED_CORRELATION_ID.value()));
    }


    @Test
    void should_generate_fixed_return_id_successfully_when_correlation_id_exists(){
        // given:
        // Pre-register a ReturnId with the same value as FIXED_CORRELATION_ID
        ReturnId existingReturnId = new ReturnId(FIXED_CORRELATION_ID.value());

        ReturnIdMappingService mappingService = new ReturnIdMappingService();
        mappingService.mapCorrelationToReturn(FIXED_CORRELATION_ID, existingReturnId);

        ReturnIdMappingService spyMappingService = spy(mappingService);
        ReturnIdGenerator fixedReturnIdGenerator = new FixedReturnIdGenerator(spyMappingService);

        // when:
        ReturnId returnId = fixedReturnIdGenerator.generate(FIXED_CORRELATION_ID);

        // then:
        // Verify method calls
        verify(spyMappingService).getReturnIdByCorrelation(FIXED_CORRELATION_ID);
        verify(spyMappingService, never()).mapCorrelationToReturn(eq(FIXED_CORRELATION_ID), any(ReturnId.class));


        // Verify actual results
        assertThat(spyMappingService.getReturnIdByCorrelation(FIXED_CORRELATION_ID)).isPresent();
        assertThat(returnId).isEqualTo(existingReturnId);

    }


    @Test
    void should_generate_random_return_id_when_correlation_id_is_null() {
        // given:
        ReturnIdMappingService mappingService = new ReturnIdMappingService();
        ReturnIdMappingService spyMappingService = spy(mappingService);
        ReturnIdGenerator fixedReturnIdGenerator = new FixedReturnIdGenerator(spyMappingService);

        // when:
        ReturnId returnId = fixedReturnIdGenerator.generate(null);

        // then:
        // Verify that a correlation ID was created and used
        verify(spyMappingService).getReturnIdByCorrelation(any(CorrelationId.class));
        verify(spyMappingService).mapCorrelationToReturn(any(CorrelationId.class), eq(returnId));

        // Verify that a ReturnId was generated
        assertThat(returnId).isNotNull();
        assertThat(returnId.id()).isNotNull();
    }
}
