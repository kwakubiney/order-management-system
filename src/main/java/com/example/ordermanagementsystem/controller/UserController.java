package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.Payload.GenericMessage;
import com.example.ordermanagementsystem.Payload.UserPayload;
import com.example.ordermanagementsystem.input.CreateUserInput;
import com.example.ordermanagementsystem.input.LoginUserInput;
import com.example.ordermanagementsystem.input.UpdateUserInput;
import com.example.ordermanagementsystem.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @MutationMapping
    public UserPayload createUser(@Argument(name = "input") CreateUserInput input) {
            return userService.createUser(input);
    }

    @MutationMapping
    public UserPayload updateUser(@Argument(name = "input") UpdateUserInput input) {
        return userService.updateUser(input);
    }

    @MutationMapping
    public GenericMessage deleteUser(@Argument(name = "id") Long id) {
        return userService.deleteUser(id);
    }

    @QueryMapping
    public UserPayload user(@Argument(name = "id") Long id) {
        return userService.user(id);
    }

    @QueryMapping
    public List<UserPayload> users() {
        return userService.users();
    }

    @QueryMapping
    public String loginUser(@Argument(name = "input") LoginUserInput input) {
        return userService.loginUser(input);
    }
}