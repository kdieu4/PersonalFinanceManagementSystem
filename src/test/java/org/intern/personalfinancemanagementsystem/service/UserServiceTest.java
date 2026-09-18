package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.UpdateProfileRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.UserProfileResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.service.impl.AuthServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void changePassword_WhenValidRequest_ShouldReturnSuccess() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String oldPassword = "Test@12345";
        String newPassword = "Test@56789";
        String confirmPassword = "Test@56789";

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(oldPassword);

        // 2. Act
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.oldPassword(), mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.newPassword(), mockUser.getPassword())).thenReturn(false);

        userService.changePassword(testEmail, request);

        // 3. Assert
        Mockito.verify(userRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    void changePassword_WhenPasswordSameAsOld_ShouldThrowException() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String oldPassword = "Test@12345";
        String newPassword = "Test@56789";
        String confirmPassword = "Test@56789";

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(oldPassword);

        // 2. Act
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.oldPassword(), mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.newPassword(), mockUser.getPassword())).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            userService.changePassword(testEmail, request);
        });
        // 3. Assert
        Assertions.assertEquals(ErrorMessage.Auth.PASSWORD_SAME_AS_OLD, caughtException.getErrorMessage());
    }

    @Test
    void changePassword_WhenInvalidOldPassword_ShouldThrowException() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String testOldPassword = "Test@12345";
        String realOldPassword = "Test@02340";
        String newPassword = "Test@56789";
        String confirmPassword = "Test@56789";

        ChangePasswordRequest request = new ChangePasswordRequest(testOldPassword, newPassword, confirmPassword);

        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setPassword(realOldPassword);

        // 2. Act
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.oldPassword(), mockUser.getPassword())).thenReturn(false);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            userService.changePassword(testEmail, request);
        });
        // 3. Assert
        Assertions.assertEquals(ErrorMessage.Auth.INVALID_PASSWORD, caughtException.getErrorMessage());
    }

    @Test
    void changePassword_WhenUserNotFound_ShouldThrowException() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        String testOldPassword = "Test@12345";
        String realOldPassword = "Test@02340";
        String newPassword = "Test@56789";
        String confirmPassword = "Test@56789";

        ChangePasswordRequest request = new ChangePasswordRequest(testOldPassword, newPassword, confirmPassword);

        // 2. Act
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            userService.changePassword(testEmail, request);
        });
        // 3. Assert
        Assertions.assertEquals(ErrorMessage.User.USER_NOT_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void getProfile_WhenEmailValid_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        UserProfileResponse response = userService.getProfile(testEmail);
        Assertions.assertNotNull(response);
    }

    @Test
    void getProfile_WhenInvalidEmail_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            userService.getProfile(testEmail);
        });
        // 3. Assert
        Assertions.assertEquals(ErrorMessage.User.USER_NOT_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void updateProfile_WhenEmailValid_ShouldReturnSuccess() {
        String testEmail = "test@gmail.com";
        String testFullName = "Hoang Thanh Dieu";
        String testPhoneNumber = "0987654321";
        LocalDate testDateOfBirth = LocalDate.of(2006, 9, 18);
        User mockUser = new User();
        mockUser.setEmail(testEmail);

        UpdateProfileRequest request = new UpdateProfileRequest(testFullName, testPhoneNumber, testDateOfBirth);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        UserProfileResponse response = userService.updateProfile(testEmail, request);
        Assertions.assertNotNull(response);
    }
}
