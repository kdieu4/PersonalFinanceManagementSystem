package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.*;
import org.intern.personalfinancemanagementsystem.domain.dto.response.LoginResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RefreshTokenResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void logout(LogoutRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void forgotPassword (ForgotPasswordRequest request);
}
