package org.intern.personalfinancemanagementsystem.service.impl;

import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.constant.KafkaConstant;
import org.intern.personalfinancemanagementsystem.constant.RedisConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.message.ForgotPasswordMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.*;
import org.intern.personalfinancemanagementsystem.domain.dto.response.LoginResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RefreshTokenResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RegisterResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Role;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.InvalidatedTokenRepository;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.AuthService;
import org.intern.personalfinancemanagementsystem.service.JwtService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    InvalidatedTokenRepository invalidatedTokenRepository;
    StringRedisTemplate stringRedisTemplate;
    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

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
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.INVALID_CREDENTIALS, ErrorMessage.NOT_FOUND_CODE));

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
            jwtService.invalidatedToken(request.refreshToken());
        } catch (ParseException e) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.INVALID_LOGOUT_TOKEN);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        // 1. Lay user
        String email = jwtService.extractEmail(request.refreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.User.USER_NOT_EXISTED, ErrorMessage.BAD_REQUEST_CODE));
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 2. check validate token
        if (jwtService.isTokenValid(request.refreshToken(), userDetails) && jwtService.isAccessToken(request.refreshToken())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.INVALID_REFRESH_TOKEN, ErrorMessage.BAD_REQUEST_CODE);
        }

        // 3. Tao token
        String newAccessToken = jwtService.generateToken(user);

        return new RefreshTokenResponse(newAccessToken);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("----forgot password---");
        if (request.emailOrPhoneNumber().contains("@")) {
            sendOtpViaEmail(request.emailOrPhoneNumber());
        } else sendOtpViaSms(request.emailOrPhoneNumber());
    }

    private void sendOtpViaSms(String phoneNumber) {
    }

    private void sendOtpViaEmail(String email) {
        log.info("----send otp via email----");
        // 1. Tim user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.User.USER_NOT_EXISTED, ErrorMessage.BAD_REQUEST_CODE));
        String key = RedisConstant.OTP_FORGOT_PASSWORD_KEY + email;
        // 2. Kiem tra user co otp chua
//        if (stringRedisTemplate.hasKey(key)) {
//            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.OTP_ALREADY_SENT, ErrorMessage.BAD_REQUEST_CODE);
//        }
        // 3. Tao va luu token
        String otp = String.format("%06d", new SecureRandom().nextInt(1000000));
        stringRedisTemplate.opsForValue().set(key, otp, RedisConstant.OTP_FORGOT_PASSWORD_TTL, TimeUnit.MINUTES);

        // 4. Tao va gui message
        try {
            ForgotPasswordMessage forgotPasswordMessage = new ForgotPasswordMessage(email, otp);
            String jsonString = objectMapper.writeValueAsString(forgotPasswordMessage);
            kafkaTemplate.send(KafkaConstant.FORGOT_PASSWORD_TOPIC, jsonString)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Kafka gửi thất bại", exception);
                        } else {
                            log.info(
                                    "Kafka gửi thành công: partition={}, offset={}",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );
                        }
                    });
        } catch (JacksonException e) {
            log.error("Lỗi gửi gửi otp");
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }
}