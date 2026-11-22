package com.iharshashkou.internetstore.service.auth;

import com.iharshashkou.internetstore.entity.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceRefreshTest extends BaseAuthServiceTest {

    @Test
    void refresh_validToken_returnsNewTokensAndRevokesOldToken() {
        String oldRefreshToken = "old-refresh-token";
        String email = "user@example.com";

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("encoded-password")
                .roles("USER")
                .build();

        RefreshToken storedToken = new RefreshToken(
                "old-token-id",
                email,
                Instant.now().plusSeconds(3600)
        );

        when(jwtService.isRefreshTokenValid(oldRefreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(oldRefreshToken)).thenReturn("old-token-id");
        when(refreshTokenRepository.findByTokenId("old-token-id"))
                .thenReturn(Optional.of(storedToken));
        when(jwtService.extractUsername(oldRefreshToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(jwtService.generateToken(userDetails)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");
        when(jwtService.extractTokenId("new-refresh-token")).thenReturn("new-token-id");
        when(jwtService.extractExpiration("new-refresh-token"))
                .thenReturn(Instant.now().plusSeconds(7200));

        var result = authService.refresh(oldRefreshToken);

        assertNotNull(result);
        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals("Bearer", result.tokenType());

        assertNotNull(storedToken.getRevokedAt());

        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
        verify(userDetailsService).loadUserByUsername(email);
    }

    @Test
    void refresh_invalidToken_throwsUnauthorized() {
        String refreshToken = "invalid-refresh-token";

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.refresh(refreshToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(refreshTokenRepository, userDetailsService);
    }

    @Test
    void refresh_tokenNotFound_throwsUnauthorized() {
        String refreshToken = "valid-refresh-token";

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(refreshToken)).thenReturn("missing-token-id");
        when(refreshTokenRepository.findByTokenId("missing-token-id"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.refresh(refreshToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void refresh_revokedToken_throwsUnauthorized() {
        String refreshToken = "revoked-refresh-token";
        String email = "user@example.com";

        RefreshToken storedToken = new RefreshToken(
                "revoked-token-id",
                email,
                Instant.now().plusSeconds(3600)
        );
        storedToken.setRevokedAt(Instant.now());

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(refreshToken)).thenReturn("revoked-token-id");
        when(refreshTokenRepository.findByTokenId("revoked-token-id"))
                .thenReturn(Optional.of(storedToken));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.refresh(refreshToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void refresh_emailMismatch_throwsUnauthorized() {
        String refreshToken = "valid-refresh-token";

        RefreshToken storedToken = new RefreshToken(
                "token-id",
                "user@example.com",
                Instant.now().plusSeconds(3600)
        );

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(refreshToken)).thenReturn("token-id");
        when(refreshTokenRepository.findByTokenId("token-id"))
                .thenReturn(Optional.of(storedToken));
        when(jwtService.extractUsername(refreshToken)).thenReturn("another@example.com");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.refresh(refreshToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void refresh_expiredStoredToken_throwsUnauthorized() {
        String refreshToken = "expired-refresh-token";

        RefreshToken storedToken = new RefreshToken(
                "expired-token-id",
                "user@example.com",
                Instant.now().minusSeconds(3600)
        );

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(refreshToken)).thenReturn("expired-token-id");
        when(refreshTokenRepository.findByTokenId("expired-token-id"))
                .thenReturn(Optional.of(storedToken));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.refresh(refreshToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}
