package org.intern.personalfinancemanagementsystem.service;

import com.nimbusds.jwt.SignedJWT;
import org.intern.personalfinancemanagementsystem.domain.entity.User;

public interface JwtService {
    String generateToken(User user);
    String generateRefreshToken(User user);
    void invalidatedToken(SignedJWT signedJWT);
    boolean isAccessToken(SignedJWT signedJWT);
    void cleanUpExpiredTokens();
}
