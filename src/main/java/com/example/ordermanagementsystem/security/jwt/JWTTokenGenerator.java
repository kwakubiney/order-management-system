package com.example.ordermanagementsystem.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.ordermanagementsystem.security.Utils;
import com.example.ordermanagementsystem.security.TokenGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class JWTTokenGenerator implements TokenGenerator {

    private final Utils jwtUtils;
    @Override
    public String build(Object id, Object role) {
        return JWT.create()
                .withSubject(id.toString())
                .withClaim("role", role.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtUtils.getExpirationTime()))
                .sign(Algorithm.HMAC512(jwtUtils.getSecret().getBytes()));
    }
}
