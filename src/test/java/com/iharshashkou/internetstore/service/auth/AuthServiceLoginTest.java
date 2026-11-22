package com.iharshashkou.internetstore.service.auth;

import com.iharshashkou.internetstore.DTO.auth.AuthTokens;
import com.iharshashkou.internetstore.DTO.auth.LoginRequest;
import com.iharshashkou.internetstore.entity.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceLoginTest extends BaseAuthServiceTest{

    @Test
    void login_shouldReturnTokens_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest(
                "ivan@example.com",
                "password123"
        );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("ivan@example.com")
                        .password("encodedPassword")
                        .roles("USER")
                        .build();

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

        AuthTokens result = authService.login(request);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("Bearer", result.tokenType());

        verify(authenticationManager).authenticate(any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest(
                "ivan@example.com",
                "wrongPassword"
        );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(userDetailsService, jwtService, refreshTokenRepository);
    }
}