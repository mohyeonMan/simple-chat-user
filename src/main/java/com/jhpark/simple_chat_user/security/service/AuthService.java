package com.jhpark.simple_chat_user.security.service;


import com.jhpark.simple_chat_user.security.dto.JwtResponse;
import com.jhpark.simple_chat_user.security.entity.RefreshToken;
import com.jhpark.simple_chat_user.security.repository.RefreshTokenRepository;
import com.jhpark.simple_chat_user.security.util.JwtUtil;
import com.jhpark.simple_chat_user.user.entity.User;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public JwtResponse authenticateUser(final User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);
        return new JwtResponse(accessToken, refreshToken);
    }
    
    public JwtResponse refreshAccessToken(final String refreshToken) {
        final User user = jwtUtil.getUserFromToken(refreshToken);
        final Long userId = user.getId();

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));
        if (storedToken.getUserId().equals(userId)) {
            refreshTokenRepository.delete(storedToken);
            return authenticateUser(user);
        }
        throw new RuntimeException("유효하지 않은 토큰입니다.");
    }

    public String createRefreshToken(final User user) {
        String refreshToken = jwtUtil.generateRefreshToken(user);
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .build());
        return refreshToken;
    }

    public void checkAccessTokenAndSetAuthentication(final String accessToken){
        final Authentication auth = getAuthentication(accessToken);

        SecurityContextHolder.getContext().setAuthentication(auth);        
    }

    public Authentication getAuthentication(final String token){
        final User user = jwtUtil.getUserFromToken(token);

        final List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(
            user.getAuthorities().toString()));

        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

}