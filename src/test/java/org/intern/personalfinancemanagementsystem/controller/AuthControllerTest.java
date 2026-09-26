package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.request.*;
import org.intern.personalfinancemanagementsystem.domain.dto.response.LoginResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RefreshTokenResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RegisterResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.VerifyOtpResponse;
import org.intern.personalfinancemanagementsystem.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private LogoutRequest logoutRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private VerifyOtpRequest verifyOtpRequest;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void register_whenValidRequest_shouldReturnCreated() {
        RegisterRequest request = mock(RegisterRequest.class);

        RegisterResponse serviceResponse = mock(RegisterResponse.class);
        when(authService.register(request)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<RegisterResponse>> response = authController.register(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(authService).register(request);
    }

    @Test
    void login_whenValidRequest_shouldReturnOk() {
        LoginRequest request = mock(LoginRequest.class);
        LoginResponse serviceResponse = mock(LoginResponse.class);

        when(authService.login(request)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).login(request);
    }

    @Test
    void refreshToken_whenValidRequest_shouldReturnOk() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshTokenResponse serviceResponse = mock(RefreshTokenResponse.class);

        when(authService.refreshToken(request)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<RefreshTokenResponse>> response = authController.refreshToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).refreshToken(request);
    }

    @Test
    void forgotPassword_whenValidRequest_shouldReturnOk() {
        ForgotPasswordRequest request = mock(ForgotPasswordRequest.class);
        ResponseEntity<ApiResponse<Void>> response = authController.forgotPassword(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).forgotPassword(request);
    }

    @Test
    void verifyOtp_whenValidRequest_shouldReturnOk() {
        VerifyOtpRequest request = mock(VerifyOtpRequest.class);
        VerifyOtpResponse serviceResponse = mock(VerifyOtpResponse.class);
        when(authService.verifyOtp(request)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<VerifyOtpResponse>> response = authController.verifyOtp(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).verifyOtp(request);
    }

    @Test
    void resetPassword_whenValidRequest_shouldReturnOk() {
        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        ResponseEntity<ApiResponse<Void>> response = authController.resetPassword(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).resetPassword(request);
    }

    @Test
    void logout_whenValidRequest_shouldReturnOk() {
        LogoutRequest request = mock(LogoutRequest.class);
        ResponseEntity<ApiResponse<Void>> response = authController.logout(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).logout(request);
    }
}
