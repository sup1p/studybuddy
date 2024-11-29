package com.studybuddy.demostud.Config;

import com.studybuddy.demostud.Service.CustomUserDetailsService;
import com.studybuddy.demostud.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Генерируем секретный ключ

    Map<String, Object> claims = new HashMap<>();

    public String generateToken(UserDetails userDetails) {

        // Add roles to claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Устанавливаем имя пользователя
                .setIssuedAt(new Date()) // Дата выпуска токена
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 часа
                .signWith(key) // Подписываем токен секретным ключом
                .compact();
    }

    // TAKE EMAIL FROM TOKEN
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // IS TOKEN VALID
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Токен недействителен
        }
    }
}

