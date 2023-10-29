package com.example.ordermanagementsystem.services;
import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.input.*;
import com.example.ordermanagementsystem.payload.UserPayload;
import com.example.ordermanagementsystem.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityMapper entityMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserPayload createUserPayload;
    private User user;

    private CreateUserInput createUserInput;

    @BeforeEach
    public void init() {
        user = new User(1L, "kwaku", "k@mail.com", "1131", User.Role.ADMIN, null);
        createUserInput = new CreateUserInput("kwaku", "k@mail.com", "1131", User.Role.ADMIN);
        createUserPayload = new UserPayload(1L, "name", "k@mail.com", User.Role.ADMIN);
    }

    @Test
    void createUser_ShouldSucceed() {
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any())).thenReturn(user);
        Mockito.when(passwordEncoder.encode(any())).thenReturn("1131");
        Mockito.when(entityMapper.userToUserPayload(any())).thenReturn(createUserPayload);
        var savedUser = userService.createUser(createUserInput);
        Assertions.assertThat(savedUser).isEqualTo
                (createUserPayload);
    }
    @Test
    void createUser_ShouldThrowExceptionIfUsernameAlreadyExists() {
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Assertions.assertThatThrownBy(() -> {
                    userService.createUser(createUserInput);
                }).isInstanceOf(CustomGraphQLException.class)
                .hasMessage(String.format("User with email %s already exists", user.getEmail()));
    }
}
