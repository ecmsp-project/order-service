package com.ecmsp.orderservice.e2e.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class InternalOrderApi {

    private final URI baseUri;

    public InternalOrderApi(URI baseUri) {
        this.baseUri = baseUri;
    }

    public void createOrderMapping(String correlationId, String orderId) {
        try {
            String jsonPayload = String.format(
                    "{\"correlationId\":\"%s\",\"orderId\":\"%s\"}",
                    correlationId, orderId
            );

            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUri + "/api/internal/orders/order-id-mappings"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200) {
                throw new RuntimeException("Failed to create order mapping, status code: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
