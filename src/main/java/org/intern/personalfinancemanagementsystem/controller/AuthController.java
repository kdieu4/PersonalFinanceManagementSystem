package org.intern.personalfinancemanagementsystem.controller;

import com.sun.net.httpserver.Authenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.base.RestData;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.RegisterRequest;
import org.intern.personalfinancemanagementsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@Tag(name = "Authentication")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<ApiResponse<RestData<Void>>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(HttpStatus.OK, SuccessMessage.Auth.REGISTER_SUCCESS, null);
    }
}
