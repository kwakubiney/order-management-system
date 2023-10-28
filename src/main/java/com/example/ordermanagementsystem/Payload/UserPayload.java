package com.example.ordermanagementsystem.Payload;

import com.example.ordermanagementsystem.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPayload {
    private Long id;
    private String name;
    private String email;
    private User.Role role;
}