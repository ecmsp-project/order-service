package com.ecmsp.orderservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static com.ecmsp.orderservice.utils.MockMvcAssertionsUtils.OBJECT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;

public class MockHttpServletResponseAssert {

    private final MockHttpServletResponse response;

    MockHttpServletResponseAssert(MockHttpServletResponse response) {
        this.response = response;
    }

    public void hasStatus(int expectedStatus) {
        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    public void hasContentType(String expectedContentType) {
        assertThat(response.getContentType()).isEqualTo(expectedContentType);
    }

    public void hasJsonBody(String expectedJson) throws UnsupportedEncodingException, JsonProcessingException {
        var actualResponseBody = OBJECT_MAPPER.readTree(response.getContentAsString(Charset.defaultCharset()));
        var expectedResponseBody = OBJECT_MAPPER.readTree(expectedJson);

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

}
