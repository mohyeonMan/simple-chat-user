package com.jhpark.simple_chat_user.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_user.user.dto.UserDTO;
import com.jhpark.simple_chat_user.user.entity.User;
import com.jhpark.simple_chat_user.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        userRepository.save(User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build());
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
}
