package com.iharshashkou.internetstore.service.auth;

import com.iharshashkou.internetstore.entity.RefreshToken;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceLogoutTest extends BaseAuthServiceTest {

    @Test
    void logout_nullToken_doesNothing() {
        authService.logout(null);

        verifyNoInteractions(jwtService, refreshTokenRepository);
    }

    @Test
    void logout_blankToken_doesNothing() {
        authService.logout("   ");

        verifyNoInteractions(jwtService, refreshTokenRepository);
    }

    @Test
    void logout_invalidToken_doesNothing() {
        String refreshToken = "invalid-refresh-token";

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(false);

        assertDoesNotThrow(() -> authService.logout(refreshToken));

        verify(jwtService).isRefreshTokenValid(refreshToken);
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void logout_validToken_revokesStoredToken() {
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

        assertDoesNotThrow(() -> authService.logout(refreshToken));

        assertNotNull(storedToken.getRevokedAt());

        verify(refreshTokenRepository).save(storedToken);
    }

    @Test
    void logout_tokenNotFound_doesNothing() {
        String refreshToken = "valid-refresh-token";

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(refreshToken)).thenReturn("missing-token-id");
        when(refreshTokenRepository.findByTokenId("missing-token-id"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> authService.logout(refreshToken));

        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void logout_alreadyRevokedToken_doesNotSaveAgain() {
        String refreshToken = "revoked-refresh-token";

        RefreshToken storedToken = new RefreshToken(
                "revoked-token-id",
                "user@example.com",
                Instant.now().plusSeconds(3600)
        );
        storedToken.setRevokedAt(Instant.now());

        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.extractTokenId(refreshToken)).thenReturn("revoked-token-id");
        when(refreshTokenRepository.findByTokenId("revoked-token-id"))
                .thenReturn(Optional.of(storedToken));

        assertDoesNotThrow(() -> authService.logout(refreshToken));

        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void logout_jwtException_doesNothing() {
        String refreshToken = "malformed-refresh-token";

        when(jwtService.isRefreshTokenValid(refreshToken))
                .thenThrow(new JwtException("Invalid token"));

        assertDoesNotThrow(() -> authService.logout(refreshToken));

        verifyNoInteractions(refreshTokenRepository);
    }
}
