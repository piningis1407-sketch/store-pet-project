package com.iharshashkou.internetstore.service;

import com.iharshashkou.internetstore.DTO.*;
import com.iharshashkou.internetstore.entity.RefreshTokenEntity;
import com.iharshashkou.internetstore.entity.UserEntity;
import com.iharshashkou.internetstore.exception.UserNotFoundException;
import com.iharshashkou.internetstore.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.security.JwtService;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthTokens register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email уже зарегистрирован"
            );
        }

        UserEntity user = UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(
                        request.password()
                ))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        return createAuthResponse(userDetails);
    }

    public AuthTokens login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        request.email()
                );

        return createAuthResponse(userDetails);
    }

    private AuthTokens createAuthResponse(UserDetails userDetails) {
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        RefreshTokenEntity entity = new RefreshTokenEntity(
                jwtService.extractTokenId(refreshToken),
                userDetails.getUsername(),
                jwtService.extractExpiration(refreshToken)
        );

        refreshTokenRepository.save(entity);

        return new AuthTokens(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        try {
            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                throw unauthorized();
            }

            String tokenId = jwtService.extractTokenId(refreshToken);

            RefreshTokenEntity storedToken = refreshTokenRepository
                    .findByTokenId(tokenId)
                    .orElseThrow(this::unauthorized);

            if (!storedToken.isActive()) {
                throw unauthorized();
            }

            String username = jwtService.extractUsername(refreshToken);

            if (!storedToken.getEmail().equals(username)) {
                throw unauthorized();
            }

            // Отзываем старый refresh token.
            storedToken.setRevokedAt(Instant.now());
            refreshTokenRepository.save(storedToken);

            // Выдаём новую пару токенов.
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            return createAuthResponse(userDetails);

        } catch (JwtException | IllegalArgumentException exception) {
            throw unauthorized();
        }
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or revoked refresh token"
        );
    }

    public UserProfileResponse getCurrentUserProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(email)
                );

        return new UserProfileResponse(
                user.getName(),
                user.getEmail()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        try {
            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                return;
            }

            String tokenId = jwtService.extractTokenId(refreshToken);

            refreshTokenRepository.findByTokenId(tokenId)
                    .ifPresent(token -> {
                        if (token.isActive()) {
                            token.setRevokedAt(Instant.now());
                            refreshTokenRepository.save(token);
                        }
                    });

        } catch (JwtException | IllegalArgumentException exception) {
            // Выход должен быть идемпотентным:
            // невалидный или уже истёкший токен не мешает очистить сессию.
        }
    }
}
