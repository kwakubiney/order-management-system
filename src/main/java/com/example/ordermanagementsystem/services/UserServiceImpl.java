package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Payload.GenericMessage;
import com.example.ordermanagementsystem.Payload.UserPayload;
import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.input.CreateUserInput;
import com.example.ordermanagementsystem.input.LoginUserInput;
import com.example.ordermanagementsystem.input.UpdateUserInput;
import com.example.ordermanagementsystem.repository.UserRepository;
import com.example.ordermanagementsystem.security.TokenGenerator;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserPayload createUser(CreateUserInput input) {
        Optional<User> existingUser = userRepository.findUserByEmail(input.getEmail());
        if (existingUser.isPresent()){
            throw new CustomGraphQLException(String.format("User with email %s already exists", input.getEmail()), 400);
        }
        User userToBeSaved = User.builder().email(input.getEmail())
                .name(input.getName())
                .password(passwordEncoder.encode(input.getPassword()))
                .role(input.getRole())
                .build();
        User savedUser = userRepository.save(userToBeSaved);
        return entityMapper.userToUserPayload(savedUser);
    }

    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public UserPayload user(Long id) {
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        if (!existingUser.get().getId().equals(id)){
            throw new CustomGraphQLException("Not authorized to view this user's details", 401);
        }
        return entityMapper.userToUserPayload(existingUser.get());
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<UserPayload> users() {
        return userRepository.findAll().stream().map(
                (user)-> UserPayload.builder().
                        id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole())
                        .build()
        ).collect(Collectors.toList());
    }

    @Secured("ROLE_NORMAL")
    @Override
    public UserPayload updateUser(UpdateUserInput payload){
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", payload.getId()), 404);
        }
        if (!existingUser.get().getId().equals(payload.getId())){
            throw new CustomGraphQLException("Not authorized to view this user's details", 401);
        }
        var updatedUser = userRepository.save(entityMapper.updateUserInputToUser(existingUser.get(), payload));
        return entityMapper.userToUserPayload(updatedUser);
        }

    @Secured("ROLE_NORMAL")
    @Override
    public GenericMessage deleteUser(Long id) {
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", id), 404);
        }
        if (!existingUser.get().getId().equals(id)){
            throw new CustomGraphQLException("Not authorized to view this user's details", 401);
        }
        userRepository.deleteById(id);
        return new GenericMessage(String.format("User with id %s has successfully been deleted", id));
    }

    @Override
    public String loginUser(LoginUserInput input) {
        var existingUser = userRepository.findUserByEmail(input.getEmail());
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with email %s does not exist", input.getEmail()), 404);
        }

        if (!passwordEncoder.matches(input.getPassword(), existingUser.get().getPassword())){
            throw new CustomGraphQLException("Invalid credentials", 401);
        }
        return tokenGenerator.build(existingUser.get().getEmail(), existingUser.get().getRole());
    }
}