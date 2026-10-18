package com.iharshashkou.internetstore.DTO;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        String tokenType
) {}