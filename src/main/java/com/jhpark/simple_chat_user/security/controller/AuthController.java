package com.jhpark.simple_chat_user.security.controller;

import com.jhpark.simple_chat_user.security.dto.JwtResponse;
import com.jhpark.simple_chat_user.security.dto.LoginRequest;
import com.jhpark.simple_chat_user.security.entity.RefreshToken;
import com.jhpark.simple_chat_user.security.service.AuthService;
import com.jhpark.simple_chat_user.security.util.JwtUtil;
import com.jhpark.simple_chat_user.user.entity.User;
import com.jhpark.simple_chat_user.user.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(authService.authenticateUser(user));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }
}