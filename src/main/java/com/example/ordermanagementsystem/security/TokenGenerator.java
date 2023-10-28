package com.example.ordermanagementsystem.security;

public interface TokenGenerator {
    String build(Object id, Object role);
}
