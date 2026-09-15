package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.User;

public record VerifyOtpResponse(
        String email
) {
    public static VerifyOtpResponse from(User user) {
        return new VerifyOtpResponse(user.getEmail());
    }
}
