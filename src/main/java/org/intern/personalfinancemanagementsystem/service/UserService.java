package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.UpdateProfileRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.UserProfileResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.User;

import java.util.UUID;

public interface UserService {
    UserProfileResponse getProfile(String email);

    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);

    void changePassword(String email, ChangePasswordRequest request);

    User findByEmail(String email);

    User findById(UUID userId);
    User getReferenceById(UUID userId);
}
