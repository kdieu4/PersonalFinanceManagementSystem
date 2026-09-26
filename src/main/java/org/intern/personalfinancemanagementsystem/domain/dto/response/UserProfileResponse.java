package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.User;

import java.time.LocalDate;

public record UserProfileResponse(
        String fullName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        String avatar
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getFullName(), user.getEmail(), user.getPhoneNumber(), user.getDateOfBirth(), user.getAvatar());
    }
}
