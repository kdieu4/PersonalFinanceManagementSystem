package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.UpdateProfileRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.UserProfileResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(String email) {
        return UserProfileResponse.from(findByEmail(email));
    }

    @Override
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);

        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());
        user.setDateOfBirth(request.dateOfBirth());

        return UserProfileResponse.from(userRepository.save(user));
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        // 1. Xác thuc email
        User user = findByEmail(email);
        // 2. Kiem tra mk cu
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.INVALID_PASSWORD, ErrorMessage.BAD_REQUEST_CODE);
        }
        // 3. Kiem tra mk cu co trung mk moi ko
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.PASSWORD_SAME_AS_OLD, ErrorMessage.BAD_REQUEST_CODE);
        }
        // 4. Thay doi mk
        String password = passwordEncoder.encode(request.newPassword());
        user.setPassword(password);
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.User.USER_NOT_EXISTED));
    }
}
