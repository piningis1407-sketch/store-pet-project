package com.iharshashkou.internetstore.DTO.auth;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        String tokenType
) {}