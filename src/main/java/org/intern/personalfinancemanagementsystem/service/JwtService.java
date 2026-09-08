package org.intern.personalfinancemanagementsystem.service;

import com.nimbusds.jwt.SignedJWT;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(User user);
    String generateRefreshToken(User user);
    void invalidatedToken(SignedJWT signedJWT);
    boolean isAccessToken(String token);
    void cleanUpExpiredTokens();
    String extractEmail(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
