
package com.jhpark.simple_chat_user.security.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}