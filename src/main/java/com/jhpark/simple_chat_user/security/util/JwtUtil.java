package com.jhpark.simple_chat_user.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_user.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtil {

    private final Key secretKey; 
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 900000; // 15분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 604800000; // 7일
    
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateAccessToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString()) 
                .claim("username", user.getUsername()) 
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey)
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
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Long getUserIdFromToken(final String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public Claims parseClaims(final String token){
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
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