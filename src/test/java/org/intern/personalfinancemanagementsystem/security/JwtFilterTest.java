package org.intern.personalfinancemanagementsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.intern.personalfinancemanagementsystem.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    private JwtFilter jwtFilter;

    private final String token = "valid.jwt.token";
    private final String email = "user@example.com";

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtService, userDetailsService);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_whenAuthorizationHeaderIsMissing_shouldContinueFilterChain() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_whenAuthorizationHeaderIsNotBearer_shouldContinueFilterChain() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_whenExtractEmailReturnsNull_shouldNotSetAuthentication() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtService.extractEmail(token)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail(token);

        verifyNoInteractions(userDetailsService);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenTokenIsInvalid_shouldNotSetAuthentication() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtService.extractEmail(token)).thenReturn(email);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtService).isTokenValid(token, userDetails);

        verify(jwtService, never()).isAccessToken(token);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenTokenIsNotAccessToken_shouldNotSetAuthentication() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtService.extractEmail(token)).thenReturn(email);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        when(jwtService.isAccessToken(token)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtService).isTokenValid(token, userDetails);
        verify(jwtService).isAccessToken(token);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenSecurityContextAlreadyHasAuthentication_shouldNotAuthenticateAgain() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken existingAuthentication = new UsernamePasswordAuthenticationToken("existing-user", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractEmail(token)).thenReturn(email);
        jwtFilter.doFilterInternal(request, response, filterChain);

        assertSame(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail(token);
        verifyNoInteractions(userDetailsService);
        verify(jwtService, never()).isTokenValid(anyString(), any());
        verify(jwtService, never()).isAccessToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenJwtServiceThrowsException_shouldContinueFilterChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractEmail(token)).thenThrow(new RuntimeException("Invalid JWT"));

        assertDoesNotThrow(() -> jwtFilter.doFilterInternal(request, response, filterChain));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService).extractEmail(token);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }
}