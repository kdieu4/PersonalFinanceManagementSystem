package org.intern.personalfinancemanagementsystem.security;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    private CustomUserDetailsService customUserDetailsService;

    private String email;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);

        email = "user@example.com";
    }

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);

        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowAppException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        assertEquals(ErrorMessage.User.USER_NOT_EXISTED, exception.getMessage());

        verify(userRepository).findByEmail(email);
    }
}