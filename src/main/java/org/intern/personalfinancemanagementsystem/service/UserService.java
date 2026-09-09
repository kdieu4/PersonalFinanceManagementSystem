package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(String email);
    void changePassword(String email, ChangePasswordRequest request);
}
