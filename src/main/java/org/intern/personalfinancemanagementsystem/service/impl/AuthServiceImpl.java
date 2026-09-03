package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.RegisterRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.RegisterResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Role;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // 1. Kiem tra password
        if (!request.password().equals(request.confirmPassword())){
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.PASSWORD_MISMATCH);
        }
        // 2. Kiem tra email
        if (userRepository.existsByEmail(request.email())){
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.User.EMAIL_EXISTED);
        }
//        3. Tạo user
        User user = User.builder()
                .email(request.email())
                .password(request.email())
                .role(Role.USER)
                .isActive(true)
                .build();

//        4. Luu db
        User savedUser = userRepository.save(user);
        return RegisterResponse.from(savedUser);
    }
}