package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.DTO.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.iharshashkou.internetstore.service.AuthService;
import org.springframework.beans.factory.annotation.Value;

import java.security.Principal;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofMillis(refreshExpirationMs))
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            Principal principal
    ) {
        UserProfileResponse profile =
                authService.getCurrentUserProfile(principal.getName());

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthTokens tokens = authService.register(request);

        ResponseCookie cookie =
                createRefreshCookie(tokens.refreshToken());

        AuthResponse body = new AuthResponse(
                tokens.accessToken(),
                tokens.tokenType()
        );

        return ResponseEntity.status(201)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthTokens tokens = authService.login(request);

        ResponseCookie cookie =
                createRefreshCookie(tokens.refreshToken());

        AuthResponse body = new AuthResponse(
                tokens.accessToken(),
                tokens.tokenType()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(
                    name = "refreshToken",
                    required = false
            ) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401)
                    .header(
                            HttpHeaders.SET_COOKIE,
                            clearRefreshCookie().toString()
                    )
                    .build();
        }

        AuthTokens tokens = authService.refresh(refreshToken);

        AuthResponse body = new AuthResponse(
                tokens.accessToken(),
                tokens.tokenType()
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        createRefreshCookie(tokens.refreshToken()).toString()
                )
                .body(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = "refreshToken",
                    required = false
            ) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }

        return ResponseEntity.noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        clearRefreshCookie().toString()
                )
                .build();
    }
}
