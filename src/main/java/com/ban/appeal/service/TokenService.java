package com.ban.appeal.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ban.appeal.dto.DiscordUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${token.secret}")
    private String secret;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String generateDiscordUserToken(DiscordUser user) {
        try {
            return JWT
                .create()
                .withIssuer("appeal")
                .withSubject(new Gson().toJson(user))
                .withExpiresAt(generateExpirationDate())
                .sign(getAlgorithm());
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
    }

    public DiscordUser validateToken(String token) {
        try {
            String json = JWT
                .require(getAlgorithm())
                .withIssuer("appeal")
                .build()
                .verify(token)
                .getSubject();

            return new Gson().fromJson(json, DiscordUser.class);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime
            .now()
            .plusHours(2)
            .toInstant(ZoneOffset.of("-03:00"));
    }
}
