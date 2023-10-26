package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Dto.GenericMessage;
import com.example.ordermanagementsystem.Dto.UserPayload;
import com.example.ordermanagementsystem.payload.CreateUserInput;
import com.example.ordermanagementsystem.payload.UpdateUserInput;

import java.util.List;

public interface UserService {
    UserPayload createUser(CreateUserInput payload);
    UserPayload user(Long id);
    List<UserPayload> users();
    UserPayload updateUser(UpdateUserInput payload);
    GenericMessage deleteUser(Long id);
}