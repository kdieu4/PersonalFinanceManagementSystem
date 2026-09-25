package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.ChangePasswordRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.UpdateProfileRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.UserProfileResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetails principal;

    @Mock
    private UpdateProfileRequest updateProfileRequest;

    @Mock
    private ChangePasswordRequest changePasswordRequest;

    private UserController userController;

    private String username;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);

        username = "dieuhoang";

        when(principal.getUsername()).thenReturn(username);
    }

    @Test
    void getProfile_whenUserExists_shouldReturnOk() {
        UserProfileResponse serviceResponse = mock(UserProfileResponse.class);

        when(userService.getProfile(username)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.getProfile(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService).getProfile(username);
    }

    @Test
    void updateProfile_whenValidRequest_shouldReturnOk() {
        // Arrange
        UserProfileResponse serviceResponse = mock(UserProfileResponse.class);

        when(userService.updateProfile(username, updateProfileRequest)).thenReturn(serviceResponse);

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.updateProfile(principal, updateProfileRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());


        verify(userService).updateProfile(username, updateProfileRequest);
    }

    @Test
    void changePassword_whenValidRequest_shouldReturnOk() {
        // Act
        ResponseEntity<ApiResponse<Void>> response = userController.changePassword(principal, changePasswordRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService).changePassword(username, changePasswordRequest);
    }
}
