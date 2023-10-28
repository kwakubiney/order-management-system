package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        var existingUser = userRepository.findUserByEmail(username);
        if (existingUser.isEmpty()){
            throw new UsernameNotFoundException(username);
        }
        UserDetails user = User.withUsername(existingUser.get().getEmail()).password(existingUser.get().getPassword()).authorities(existingUser.get().getRole()).build();
        return user;
    }
}