package com.example.ordermanagementsystem.input;

import com.example.ordermanagementsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserInput {
    private String name;
    private String email;
    private String password;
    private User.Role role;
}