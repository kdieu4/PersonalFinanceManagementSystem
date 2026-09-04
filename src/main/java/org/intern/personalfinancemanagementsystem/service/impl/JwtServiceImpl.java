package org.intern.personalfinancemanagementsystem.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.service.JwtService;
import org.intern.personalfinancemanagementsystem.util.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.intern.personalfinancemanagementsystem.util.TokenType.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.expiryHour}")
    long expiryHour;

    @Value("${jwt.accessKey}")
    String accessKey;

    @Value("${jwt.refreshKey}")
    String refreshKey;

    @Value("${jwt.resetKey}")
    String resetKey;

    @Override
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String, Object> claims, User user) {
        log.info("----Generate Token----");
        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryHour))
                .signWith(getKey(ACCESS_TOKEN))
                .compact();
    }

    private Key getKey(TokenType type) {
        log.info("----getKey----");
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            case RESET_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));
            }
            default -> throw new AppException("Invalid token type");
        }
    }
}
