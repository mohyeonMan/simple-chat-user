package com.jhpark.simple_chat_user.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_user.user.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); 
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 900000; // 15분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 604800000; // 7일

    public String generateAccessToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString()) 
                .claim("username", user.getUsername()) 
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString()) 
                .claim("username", user.getUsername()) 
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public Long getUserIdFromToken(final String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public Claims parseClaims(final String token){
        try {
            return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new RuntimeException("토큰 파싱에 실패했습니다.");
        }
    }

    public User getUserFromToken(final String token) {
        Claims claims = parseClaims(token); // JWT에서 Claims 파싱
        Long id = Long.parseLong(claims.getSubject()); // subject에 저장된 id 가져오기
        String username = claims.get("username", String.class); // username 가져오기
        List<String> roles = claims.get("roles", List.class); // roles 가져오기
    
        // User 객체 생성 (필요한 경우 roles를 GrantedAuthority로 변환)
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }


}