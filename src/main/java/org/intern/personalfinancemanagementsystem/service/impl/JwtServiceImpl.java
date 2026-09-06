package org.intern.personalfinancemanagementsystem.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.constant.JwtConstant;
import org.intern.personalfinancemanagementsystem.domain.entity.InvalidatedToken;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.InvalidatedTokenRepository;
import org.intern.personalfinancemanagementsystem.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.access.expiration}")
    long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    long refreshTokenExpiration;

    @Value("${jwt.secretKey}")
    String secretKey;

    final InvalidatedTokenRepository invalidatedTokenRepository;

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
                    .claim(JwtConstant.TOKEN_TYPE_KEY, JwtConstant.ACCESS_TOKEN_TYPE)
                    .build();

            // 2. Ky
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            signedJWT.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.Auth.GENERATE_JWT_ERROR);
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
                    .claim(JwtConstant.TOKEN_TYPE_KEY, JwtConstant.REFRESH_TOKEN_TYPE)
                    .build();

            // 2. Ky
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            signedJWT.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.Auth.GENERATE_JWT_ERROR);
        }
    }

    @Override
    public void invalidatedToken(SignedJWT signedJWT) {
        try {
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(signedJWT.getJWTClaimsSet().getJWTID())
                    .expiryTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (ParseException e) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.ERR_GET_TOKEN_CLAIM_SET_FAIL);
        }
    }

    @Override
    public boolean isAccessToken(SignedJWT signedJWT) {
        log.info("----Check is access token----");
        try {
            String tokenType = signedJWT.getJWTClaimsSet().getStringClaim(JwtConstant.TOKEN_TYPE_KEY);
            return JwtConstant.ACCESS_TOKEN_TYPE.equals(tokenType);
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * * ")
    public void cleanUpExpiredTokens() {
        invalidatedTokenRepository.deleteExpiredTokens();
    }
}
