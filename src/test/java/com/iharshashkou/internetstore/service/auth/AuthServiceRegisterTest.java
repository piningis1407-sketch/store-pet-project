package com.iharshashkou.internetstore.service.auth;

import com.iharshashkou.internetstore.DTO.auth.AuthTokens;
import com.iharshashkou.internetstore.DTO.auth.RegisterRequest;
import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.entity.RefreshToken;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceRegisterTest extends BaseAuthServiceTest{

    @Test
    void register_shouldCreateUserAndReturnTokens_whenRequestIsValid() {
        RegisterRequest request = new RegisterRequest(
                "Ivan Ivanov",
                "ivan@example.com",
                "password123"
        );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("ivan@example.com")
                        .password("encodedPassword")
                        .roles("USER")
                        .build();

        when(userRepository.existsByEmail("ivan@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userDetailsService.loadUserByUsername("ivan@example.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(userDetails))
                .thenReturn("refresh-token");

        when(jwtService.extractTokenId("refresh-token"))
                .thenReturn("token-id");

        when(jwtService.extractExpiration("refresh-token"))
                .thenReturn(Instant.parse("2027-01-01T00:00:00Z"));

        AuthTokens result = authService.register(request);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("Bearer", result.tokenType());

        verify(userRepository).save(argThat(user ->
                user.getName().equals("Ivan Ivanov")
                        && user.getEmail().equals("ivan@example.com")
                        && user.getPassword().equals("encodedPassword")
                        && user.getRole() == Role.USER
        ));

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Ivan Ivanov",
                "ivan@example.com",
                "password123"
        );

        when(userRepository.existsByEmail("ivan@example.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(userRepository, never()).save(any(User.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        verifyNoInteractions(passwordEncoder, userDetailsService, jwtService);
    }
}
