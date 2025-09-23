package com.ecmsp.orderservice.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenReader {

    public JwtClaims readToken(String token) {
        try {
            // Remove signature part to avoid validation
            String[] parts = token.split("\\.");
            String unsignedToken = parts[0] + "." + parts[1] + ".";

            Claims claims = Jwts.parser()
                    .unsecured() // This allows parsing without signature verification
                    .build()
                    .parseUnsecuredClaims(unsignedToken)
                    .getPayload();

            return new JwtClaims(
                    claims.getSubject(),
                    claims.get("login", String.class),
                    claims.getIssuedAt().getTime() / 1000,
                    claims.getExpiration().getTime() / 1000
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }


}
