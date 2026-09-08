package org.intern.personalfinancemanagementsystem.service.impl;

import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.LoginRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.LogoutRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.RegisterRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.LoginResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RegisterResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Role;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.InvalidatedTokenRepository;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.service.AuthService;
import org.intern.personalfinancemanagementsystem.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. Kiem tra password
        if (!request.password().equals(request.confirmPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.PASSWORD_MISMATCH);
        }
        // 2. Kiem tra email
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.User.EMAIL_EXISTED);
        }
//        3. Encoder password
        String password = passwordEncoder.encode(request.password());
//        4. Tạo user
        User user = User.builder()
                .email(request.email())
                .password(password)
                .role(Role.USER)
                .isActive(true)
                .build();

//        5. Luu db
        User savedUser = userRepository.save(user);
        return RegisterResponse.from(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. Kiem tra email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.INVALID_CREDENTIALS));

        // 2. Kiem tra active
        if (!user.getIsActive()) {
            throw new AppException(HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN_MESSAGE);
        }

        // 3. Kiem tra password
        boolean auth = passwordEncoder.matches(request.password(), user.getPassword());
        if (!auth) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.INVALID_CREDENTIALS);
        }

        // 4. Sinh token
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 5. Tra ve
        return new LoginResponse(user.getEmail(), refreshToken, accessToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(LogoutRequest request) {
        try {
            // 1. Lay id
            SignedJWT signedJWT = SignedJWT.parse(request.refreshToken());
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            // 2. Xac thuc token
            if (jwtService.isAccessToken(request.refreshToken())) {
                throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.INVALID_LOGOUT_TOKEN);
            }
            if (invalidatedTokenRepository.existsById(jti)) {
                throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.TOKEN_ALREADY_INVALIDATED);
            }
            // 3. Luu thong tin vua lay vao db
            jwtService.invalidatedToken(signedJWT);
        } catch (ParseException e) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.INVALID_LOGOUT_TOKEN);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }
}