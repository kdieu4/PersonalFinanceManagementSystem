package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.UpdateProfileRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.UserProfileResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@Tag(name = "user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @Operation(summary = "Xem hồ sơ cá nhân", description = "Trả về thông tin của chính người dùng đang đăng nhập.")
    @GetMapping(UrlConstant.User.GET_PROFILE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        UserProfileResponse response = userService.getProfile(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.User.GET_PROFILE_SUCCESS, response));
    }

    @Operation(summary = "Cập nhật hồ sơ cá nhân")
    @PutMapping(UrlConstant.User.UPDATE_PROFILE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse response = userService.updateProfile(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.User.UPDATE_PROFILE_SUCCESS , response));
    }

    @Operation(summary = "Thay đổi mật khẩu")
    @PostMapping(UrlConstant.User.CHANGE_PASSWORD)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.User.CHANGE_PASSWORD_SUCCESS, null));
    }
}
