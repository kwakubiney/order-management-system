package com.example.ordermanagementsystem.exception;

import lombok.Getter;

@Getter
public class CustomGraphQLException extends RuntimeException {
    private final int statusCode;
    public CustomGraphQLException(String message, int statusCode) {

        super(message);
        this.statusCode = statusCode;
    }
}
