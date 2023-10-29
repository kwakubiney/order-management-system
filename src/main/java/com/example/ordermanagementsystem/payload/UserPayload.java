package com.example.ordermanagementsystem.payload;

import com.example.ordermanagementsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserPayload {
    private Long id;
    private String name;
    private String email;
    private User.Role role;
}