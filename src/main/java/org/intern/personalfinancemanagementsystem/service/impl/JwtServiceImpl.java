package org.intern.personalfinancemanagementsystem.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.access.expiration}")
    long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    long refreshTokenExpiration;


    @Value("${jwt.secretKey}")
    String secretKey;

    @Override
    public String generateToken(User user) {
        log.info("----Generate Token----");
        try {
            // 1. Tao thong tin
            JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + accessTokenExpiration))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("authorities", user.getRole().name())
                    .claim("userId", user.getId())
                    .build();

            // 2. Ky
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            signedJWT.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(User user) {
        log.info("----Generate Refresh Token----");
        try {
            // 1. Tao thong tin
            JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            // 2. Ky
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            signedJWT.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

}
