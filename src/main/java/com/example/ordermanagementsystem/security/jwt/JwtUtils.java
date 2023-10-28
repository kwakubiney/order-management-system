package com.example.ordermanagementsystem.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.security.TokenPayload;
import com.example.ordermanagementsystem.security.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtUtils extends Utils {
    @Override
    public TokenPayload decodeToken(String authorizationHeader) {
        DecodedJWT decodedToken = JWT.require(Algorithm.HMAC512(getSecret().getBytes()))
                .build()
                .verify(authorizationHeader.replace("Bearer ", ""));
        return new TokenPayload(decodedToken.getSubject(), decodedToken.getClaim("role").as(User.Role.class));
    }
}