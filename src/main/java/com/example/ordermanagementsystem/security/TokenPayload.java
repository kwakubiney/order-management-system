package com.example.ordermanagementsystem.security;

import com.example.ordermanagementsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TokenPayload {
    private final String email;
    private final User.Role role;
}