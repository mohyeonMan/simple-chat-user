package com.jhpark.simple_chat_user.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jhpark.simple_chat_user.user.dto.UserDTO;
import com.jhpark.simple_chat_user.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean isUsernameTaken = userService.isUsernameTaken(username);
        return ResponseEntity.ok(isUsernameTaken);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserDTO userDto) {
        userService.registerUser(userDto);

        return ResponseEntity.ok().build();
    }
}
