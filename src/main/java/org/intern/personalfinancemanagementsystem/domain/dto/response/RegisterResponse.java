package org.intern.personalfinancemanagementsystem.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.intern.personalfinancemanagementsystem.domain.entity.User;

public record RegisterResponse(
        @Schema(description = "Địa chỉ email đã đăng ký tài khoản")
        String email
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(user.getEmail());
    }
}
