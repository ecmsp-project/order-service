package com.ecmsp.orderservice.application.security;

public record UserContextData(
        String userId,
        String login
) {
}
