package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.constant.RedisConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.*;
import org.intern.personalfinancemanagementsystem.domain.dto.response.LoginResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RefreshTokenResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RegisterResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.VerifyOtpResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.InvalidatedTokenRepository;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.impl.AuthServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.sender.EmailOtpSender;
import org.intern.personalfinancemanagementsystem.service.impl.sender.SmsOtpSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    InvalidatedTokenRepository invalidatedTokenRepository;
    @Mock
    Map<String, OtpSender> otpSenders;
    @Mock
    StringRedisTemplate stringRedisTemplate;
    @Mock
    EmailOtpSender emailOtpSender;
    @Mock
    SmsOtpSender smsOtpSender;
    @Mock
    ValueOperations<String, String> valueOperations;
    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void login_WhenValidCredentials_ShouldReturnSuccess() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String testPassword = "Test@123";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(testPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(testPassword, mockUser.getPassword())).thenReturn(true);
        when(jwtService.generateRefreshToken(mockUser)).thenReturn("fake_jwt_refresh_token_123");
        when(jwtService.generateToken(mockUser)).thenReturn("fake_jwt_access_token_123");

        // 2. ACT
        LoginRequest request = new LoginRequest(testEmail, testPassword);
        LoginResponse response = authService.login(request);
        // 3. ASSERT
        Assertions.assertEquals("fake_jwt_access_token_123", response.accessToken());
    }

    @Test
    void login_WhenEmailNotFound_ShouldThrowException() {
        String testEmail = "notfound@gmail.com";
        String testPassword = "Test@123";

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        LoginRequest request = new LoginRequest(testEmail, testPassword);

        Assertions.assertThrows(AppException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_WhenInvalidPassword_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testPassword = "Test@12345";

        String realPassword = "Test@123";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(realPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(testPassword, mockUser.getPassword())).thenReturn(false);
        LoginRequest request = new LoginRequest(testEmail, testPassword);

        Assertions.assertThrows(AppException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_WhenUserNotActive_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testPassword = "Test@12345";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setIsActive(false);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        LoginRequest request = new LoginRequest(testEmail, testPassword);

        Assertions.assertThrows(AppException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void register_WhenValidCredentials_ShouldReturnSuccess() {
        // 1. Arrange
        String email = "test@gmail.com";
        String pass = "Test@123";
        String confirmPass = "Test@123";

        RegisterRequest request = new RegisterRequest(email, pass, confirmPass);
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(pass);

        // 2. Act
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(pass)).thenReturn("Test@12345");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(mockUser);

        RegisterResponse response = authService.register(request);
        // 3. Assert
        Assertions.assertEquals(email, response.email());
    }

    @Test
    void register_WhenEmailExists_ShouldThrowException() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String testPass = "Test@123";
        String testConfirmPass = "Test@123";

        RegisterRequest request = new RegisterRequest(testEmail, testPass, testConfirmPass);

        // 2. Act
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);
        // 3. Assert
        Assertions.assertThrows(AppException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void register_WhenPasswordNotMatch_ShouldThrowException() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String testPass = "Test@123";
        String testConfirmPass = "Test@12345";

        RegisterRequest request = new RegisterRequest(testEmail, testPass, testConfirmPass);

        // 3. Assert
        Assertions.assertThrows(AppException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void refreshToken_WhenValidToken_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        String testRefreshToken = "test_refresh_token";

        User mockUser = new User();
        mockUser.setEmail(testEmail);

        RefreshTokenRequest request = new RefreshTokenRequest(testRefreshToken);

        when(jwtService.extractEmail(testRefreshToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid(ArgumentMatchers.any(String.class), ArgumentMatchers.any(CustomUserDetails.class))).thenReturn(true);
        when(jwtService.isAccessToken(testRefreshToken)).thenReturn(false);
        when(jwtService.generateToken(mockUser)).thenReturn("fake_jwt_access_token_123");

        RefreshTokenResponse response = authService.refreshToken(request);

        Assertions.assertEquals("fake_jwt_access_token_123", response.accessToken());
    }

    @Test
    void refreshToken_WhenAccessToken_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testRefreshToken = "test_refresh_token";

        User mockUser = new User();
        mockUser.setEmail(testEmail);

        RefreshTokenRequest request = new RefreshTokenRequest(testRefreshToken);

        when(jwtService.extractEmail(testRefreshToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid(ArgumentMatchers.any(String.class), ArgumentMatchers.any(CustomUserDetails.class))).thenReturn(true);
        when(jwtService.isAccessToken(testRefreshToken)).thenReturn(true);

        Assertions.assertThrows(AppException.class, () -> {
            authService.refreshToken(request);
        });
    }

    @Test
    void refreshToken_WhenUserNotFound_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testRefreshToken = "test_refresh_token";

        RefreshTokenRequest request = new RefreshTokenRequest(testRefreshToken);

        when(jwtService.extractEmail(testRefreshToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            authService.refreshToken(request);
        });
    }

    @Test
    void refreshToken_WhenInvalidToken_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testRefreshToken = "test_refresh_token";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        CustomUserDetails testUserDetails = new CustomUserDetails(mockUser);
        RefreshTokenRequest request = new RefreshTokenRequest(testRefreshToken);

        when(jwtService.extractEmail(testRefreshToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid(ArgumentMatchers.any(String.class), ArgumentMatchers.any(CustomUserDetails.class))).thenReturn(false);
//        when(jwtService.isAccessToken(testRefreshToken)).thenReturn(false);

        Assertions.assertThrows(AppException.class, () -> {
            authService.refreshToken(request);
        });
    }

    @Test
    void logout_WhenValidToken_ShouldReturnSuccess() {
        String testRefreshToken = "test_jwt_refresh_token";
        String testJti = "test_jwt_jta";

        LogoutRequest request = new LogoutRequest(testRefreshToken);

        when(jwtService.extractJti(testRefreshToken)).thenReturn(testJti);
        when(jwtService.isAccessToken(testRefreshToken)).thenReturn(false);
        when(invalidatedTokenRepository.existsById(testJti)).thenReturn(false);

        authService.logout(request);

        Mockito.verify(jwtService, Mockito.times(1)).invalidatedToken(testRefreshToken);
    }

    @Test
    void logout_WhenValidatedToken_ShouldThrowException() {
        String testRefreshToken = "test_jwt_refresh_token";
        String testJti = "test_jwt_jta";

        LogoutRequest request = new LogoutRequest(testRefreshToken);

        when(jwtService.extractJti(testRefreshToken)).thenReturn(testJti);
        when(jwtService.isAccessToken(testRefreshToken)).thenReturn(false);
        when(invalidatedTokenRepository.existsById(testJti)).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.logout(request);
        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, caughtException.getStatus());
        Assertions.assertEquals(ErrorMessage.Auth.TOKEN_ALREADY_INVALIDATED, caughtException.getErrorMessage());
    }

    @Test
    void logout_WhenAccessToken_ShouldThrowException() {
        String testAccessToken = "test_jwt_refresh_token";
        String testJti = "test_jwt_jta";

        LogoutRequest request = new LogoutRequest(testAccessToken);

        when(jwtService.extractJti(testAccessToken)).thenReturn(testJti);
        when(jwtService.isAccessToken(testAccessToken)).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.logout(request);
        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, caughtException.getStatus());
        Assertions.assertEquals(ErrorMessage.Auth.INVALID_LOGOUT_TOKEN, caughtException.getErrorMessage());
    }

    @Test
    void forgot_WhenValidToken_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        String testForgotPasswordKey = RedisConstant.OTP_FORGOT_PASSWORD_KEY + testEmail;

        ForgotPasswordRequest request = new ForgotPasswordRequest(testEmail);

        when(otpSenders.get("email")).thenReturn(emailOtpSender);
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);
        when(stringRedisTemplate.hasKey(testForgotPasswordKey)).thenReturn(false);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        authService.forgotPassword(request);

        Mockito.verify(emailOtpSender, Mockito.times(1)).send(ArgumentMatchers.eq(testEmail), ArgumentMatchers.anyString());
    }

    @Test
    void forgot_WhenKeyExisted_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testForgotPasswordKey = RedisConstant.OTP_FORGOT_PASSWORD_KEY + testEmail;

        ForgotPasswordRequest request = new ForgotPasswordRequest(testEmail);

        when(otpSenders.get("email")).thenReturn(emailOtpSender);
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);
        when(stringRedisTemplate.hasKey(testForgotPasswordKey)).thenReturn(true);

        Assertions.assertThrows(AppException.class, () -> {
            authService.forgotPassword(request);
        });
    }

    @Test
    void forgot_WhenEmailNotFound_ShouldThrowException() {
        String testEmail = "test@gmail.com";

        ForgotPasswordRequest request = new ForgotPasswordRequest(testEmail);

        when(otpSenders.get("email")).thenReturn(emailOtpSender);
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        Assertions.assertThrows(AppException.class, () -> {
            authService.forgotPassword(request);
        });
    }

    @Test
    void forgot_WhenPhoneNumberNotFound_ShouldThrowException() {
        String testPhoneNumber = "0123456789";

        ForgotPasswordRequest request = new ForgotPasswordRequest(testPhoneNumber);

        when(otpSenders.get("sms")).thenReturn(smsOtpSender);
        when(userRepository.existsByPhoneNumber(testPhoneNumber)).thenReturn(false);

        Assertions.assertThrows(AppException.class, () -> {
            authService.forgotPassword(request);
        });
    }

    @Test
    void verifyOtp_WhenValidOtp_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        String testOtp = "012345";
        String testForgotPasswordKey = RedisConstant.OTP_FORGOT_PASSWORD_KEY + testEmail;

        VerifyOtpRequest request = new VerifyOtpRequest(testEmail, testOtp);

        User mockUser = new User();
        mockUser.setEmail(testEmail);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(testForgotPasswordKey)).thenReturn(testOtp);
        when(stringRedisTemplate.delete(testForgotPasswordKey)).thenReturn(true);

        VerifyOtpResponse response = authService.verifyOtp(request);
        Assertions.assertEquals(testEmail, response.email());
    }

    @Test
    void verifyOtp_WhenInvalidOtp_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testOtp = "012345";
        String testRealOtp = "054321";
        String testForgotPasswordKey = RedisConstant.OTP_FORGOT_PASSWORD_KEY + testEmail;

        VerifyOtpRequest request = new VerifyOtpRequest(testEmail, testOtp);

        User mockUser = new User();
        mockUser.setEmail(testEmail);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().get(testForgotPasswordKey)).thenReturn(testRealOtp);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.verifyOtp(request);
        });

        Assertions.assertEquals(ErrorMessage.Auth.INVALID_OTP, caughtException.getErrorMessage());
    }

    @Test
    void verifyOtp_WhenUserNotFound_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testOtp = "012345";

        VerifyOtpRequest request = new VerifyOtpRequest(testEmail, testOtp);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.verifyOtp(request);
        });

        Assertions.assertEquals(ErrorMessage.User.USER_NOT_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void resetPassword_WhenValidRequest_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        String testNewPassword = "Test@12345";
        String testConfirmPassword = "Test@12345";
        String testOldPassword = "Test@01234";
        String verifyOtp = "012345";

        String testKey = RedisConstant.RESET_PASSWORD_VERIFIED_KEY + testEmail;
        ResetPasswordRequest request = new ResetPasswordRequest(testEmail, testNewPassword, testConfirmPassword);

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(testOldPassword);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(testKey)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(testNewPassword, mockUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(testNewPassword)).thenReturn("Test@54321");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(mockUser);

        authService.resetPassword(request);

        Mockito.verify(stringRedisTemplate, Mockito.times(1)).delete(testKey);
    }

    @Test
    void resetPassword_WhenSameAsOld_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testNewPassword = "Test@12345";
        String testConfirmPassword = "Test@12345";
        String testOldPassword = "Test@012345";

        ResetPasswordRequest request = new ResetPasswordRequest(testEmail, testNewPassword, testConfirmPassword);
        String key = RedisConstant.RESET_PASSWORD_VERIFIED_KEY + request.identifier();

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(testOldPassword);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(testNewPassword, mockUser.getPassword())).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.resetPassword(request);
        });

        Assertions.assertEquals(ErrorMessage.Auth.PASSWORD_SAME_AS_OLD, caughtException.getErrorMessage());
    }

    @Test
    void resetPassword_WhenUserNotFound_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testNewPassword = "Test@12345";
        String testConfirmPassword = "Test@12345";
        String testOldPassword = "Test@012345";

        ResetPasswordRequest request = new ResetPasswordRequest(testEmail, testNewPassword, testConfirmPassword);
        String key = RedisConstant.RESET_PASSWORD_VERIFIED_KEY + request.identifier();

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(testOldPassword);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.resetPassword(request);
        });

        Assertions.assertEquals(ErrorMessage.User.USER_NOT_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void resetPassword_WhenResetPasswordTimeOut_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        String testNewPassword = "Test@12345";
        String testConfirmPassword = "Test@12345";
        String testOldPassword = "Test@012345";
        String testVerified = "test1@gmail.com";

        ResetPasswordRequest request = new ResetPasswordRequest(testEmail, testNewPassword, testConfirmPassword);

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(testOldPassword);

        String key = RedisConstant.RESET_PASSWORD_VERIFIED_KEY + request.identifier();

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(testVerified);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            authService.resetPassword(request);
        });

        Assertions.assertEquals(ErrorMessage.Auth.RESET_SESSION_EXPIRED, caughtException.getErrorMessage());
    }
}