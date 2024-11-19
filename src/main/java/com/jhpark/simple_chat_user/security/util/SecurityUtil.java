package com.jhpark.simple_chat_user.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jhpark.simple_chat_user.user.entity.User;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {

            return ((User) authentication.getPrincipal()).getId();
        }
        return null;
    }
}