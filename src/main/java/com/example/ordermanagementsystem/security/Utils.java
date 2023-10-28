package com.example.ordermanagementsystem.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public abstract class Utils {
    @Value("${token.header-name}")
    private String headerString;
    @Value("${token.prefix}")
    private String tokenPrefix;
    @Value("${token.secret-password}")
    private String secret;
    @Value("${token.duration-ms}")
    private long expirationTime;

    abstract public TokenPayload decodeToken(String authorizationHeader);
    
}
