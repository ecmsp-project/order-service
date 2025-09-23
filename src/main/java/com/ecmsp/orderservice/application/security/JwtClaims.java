package com.ecmsp.orderservice.application.security;

public record JwtClaims(
        String userId,
        String login,
        Long issuedAt,
        Long expiration
) {
    public boolean isExpired() {
        return System.currentTimeMillis() / 1000 > expiration;
    }
}