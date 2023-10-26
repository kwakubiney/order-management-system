package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Dto.GenericMessage;
import com.example.ordermanagementsystem.Dto.UserPayload;
import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.payload.CreateUserInput;
import com.example.ordermanagementsystem.payload.UpdateUserInput;
import com.example.ordermanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    @Override
    public UserPayload createUser(CreateUserInput payload) {
        Optional<User> existingUser = userRepository.findUserByEmail(payload.getEmail());
        if (existingUser.isPresent()){
            throw new CustomGraphQLException(String.format("User with email %s already exists", payload.getEmail()), 400);
        }
        User userToBeSaved = User.builder().email(payload.getEmail())
                .name(payload.getName())
                .password(payload.getPassword())
                .build();
        User savedUser = userRepository.save(userToBeSaved);
        return entityMapper.userToUserDTO(savedUser);
    }

    @Override
    public UserPayload user(Long id) {
        Optional<User> existingUser = userRepository.findUserById(id);
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", id), 404);
        }
        return entityMapper.userToUserDTO(existingUser.get());
    }

    @Override
    public List<UserPayload> users() {
        return userRepository.findAll().stream().map(
                (user)-> UserPayload.builder().
                        id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public UserPayload updateUser(UpdateUserInput payload){
        Optional<User> existingUser = userRepository.findUserById(payload.getId());
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", payload.getId()), 404);
        }
        entityMapper.updateFields(existingUser.get(), payload);
            var updatedUser = userRepository.save(existingUser.get());
            return entityMapper.userToUserDTO(updatedUser);
        }

    @Override
    public GenericMessage deleteUser(Long id) {
        Optional<User> existingUser = userRepository.findUserById(id);
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", id), 404);
        }
        userRepository.deleteById(id);
        return new GenericMessage(String.format("User with id %s has successfully been deleted", id));
    }
}