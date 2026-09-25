package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.entity.InvalidatedToken;
import org.intern.personalfinancemanagementsystem.domain.entity.Role;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.InvalidatedTokenRepository;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    InvalidatedTokenRepository invalidatedTokenRepository;
    @InjectMocks
    JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        String testSecretKey = "YnVmZmVyLW92ZXJmbG93LXNlY3JldC1rZXktdGVzdC1zcHJpbmctYm9vdA==";
        long testExpiration = 3600000L;
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", testExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", testExpiration);
    }

    @Test
    void generateToken_WhenValidUser_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        String token = jwtService.generateToken(mockUser);

        Assertions.assertNotNull(token);
    }

    @Test
    void generateRefreshToken_WhenValidUser_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        String token = jwtService.generateRefreshToken(mockUser);

        Assertions.assertNotNull(token);
    }

    @Test
    void invalidatedToken_WhenValidToken_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        String testRefreshToken = jwtService.generateRefreshToken(mockUser);

        jwtService.invalidatedToken(testRefreshToken);

        Mockito.verify(invalidatedTokenRepository, Mockito.times(1)).save(ArgumentMatchers.any(InvalidatedToken.class));
    }

    @Test
    void isAccessToken_WhenInvalidToken_ShouldReturnFalse() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        String testRefreshToken = jwtService.generateRefreshToken(mockUser);

        Assertions.assertFalse(jwtService.isAccessToken(testRefreshToken));
    }

    @Test
    void extractEmail_WhenValidToken_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        String testRefreshToken = jwtService.generateRefreshToken(mockUser);

        Assertions.assertEquals(testEmail, jwtService.extractEmail(testRefreshToken));
    }

    @Test
    void extractEmail_WhenInvalidToken_ShouldThrowException() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        String testToken = "aaa" + jwtService.generateToken(mockUser);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            jwtService.extractEmail(testToken);
        });

        Assertions.assertEquals(ErrorMessage.Auth.INVALID_LOGOUT_TOKEN, caughtException.getErrorMessage());
    }

    @Test
    void isTokenValid_WhenValidToken_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        UserDetails testUserDetail = new CustomUserDetails(mockUser);

        String testToken = jwtService.generateToken(mockUser);

        when(invalidatedTokenRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);

        boolean isValid = jwtService.isTokenValid(testToken, testUserDetail);

        Assertions.assertTrue(isValid);
    }

    @Test
    void extractJti_WhenValidToken_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        String testToken = jwtService.generateToken(mockUser);

        String res = jwtService.extractJti(testToken);

        Assertions.assertNotNull(res);
    }

    @Test
    void extractJti_WhenInvalidToken_ShouldThrowException() {
        String testEmail = "test@gmail.com";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        String testToken = "aaa" + jwtService.generateToken(mockUser);

        Assertions.assertThrows(AppException.class, () -> {
            jwtService.extractJti(testToken);
        });
    }

    @Test
    void isAccessToken_WhenAccessToken_ShouldReturnTrue() {
        String testEmail = "test@gmail.com";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        String testAccessToken = jwtService.generateToken(mockUser);

        boolean result = jwtService.isAccessToken(testAccessToken);

        Assertions.assertTrue(result);
    }

    @Test
    void isTokenValid_WhenTokenIsInvalidated_ShouldReturnFalse() {
        String testEmail = "test@gmail.com";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        UserDetails userDetails = new CustomUserDetails(mockUser);

        String testToken = jwtService.generateToken(mockUser);

        when(invalidatedTokenRepository.existsById(ArgumentMatchers.anyString())).thenReturn(true);

        boolean result = jwtService.isTokenValid(testToken, userDetails);

        Assertions.assertFalse(result);

        Mockito.verify(invalidatedTokenRepository).existsById(ArgumentMatchers.anyString());
    }

    @Test
    void isTokenValid_WhenEmailDoesNotMatch_ShouldReturnFalse() {
        User tokenUser = new User();
        tokenUser.setEmail("token@gmail.com");
        tokenUser.setRole(Role.USER);

        String testToken = jwtService.generateToken(tokenUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@gmail.com");
        anotherUser.setRole(Role.USER);

        UserDetails anotherUserDetails = new CustomUserDetails(anotherUser);

        when(invalidatedTokenRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);

        boolean result = jwtService.isTokenValid(testToken, anotherUserDetails);

        Assertions.assertFalse(result);
    }

    @Test
    void isTokenValid_WhenPasswordChangedAfterTokenIssued_ShouldReturnFalse() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        String testToken = jwtService.generateToken(mockUser);
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);

        when(customUserDetails.getUsername()).thenReturn(testEmail);
        Instant passwordChangedAt = Instant.now().plusSeconds(60);
        when(customUserDetails.getPasswordChangedAt()).thenReturn(passwordChangedAt);
        when(invalidatedTokenRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);

        boolean result = jwtService.isTokenValid(testToken, customUserDetails);

        Assertions.assertFalse(result);

        Mockito.verify(customUserDetails).getPasswordChangedAt();
    }

    @Test
    void isTokenValid_WhenTokenIssuedAfterPasswordChanged_ShouldReturnTrue() {
        String testEmail = "test@gmail.com";

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setRole(Role.USER);

        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getUsername()).thenReturn(testEmail);

        Instant passwordChangedAt = Instant.now().minusSeconds(60);
        when(customUserDetails.getPasswordChangedAt()).thenReturn(passwordChangedAt);

        String testToken = jwtService.generateToken(mockUser);
        when(invalidatedTokenRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);

        boolean result = jwtService.isTokenValid(testToken, customUserDetails);

        Assertions.assertTrue(result);
    }
}
