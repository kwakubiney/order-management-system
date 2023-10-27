package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Payload.GenericMessage;
import com.example.ordermanagementsystem.Payload.UserPayload;
import com.example.ordermanagementsystem.input.CreateUserInput;
import com.example.ordermanagementsystem.input.UpdateUserInput;

import java.util.List;

public interface UserService {
    UserPayload createUser(CreateUserInput payload);
    UserPayload user(Long id);
    List<UserPayload> users();
    UserPayload updateUser(UpdateUserInput payload);
    GenericMessage deleteUser(Long id);
}