package com.iharshashkou.internetstore.DTO.auth;

public record AuthResponse(
        String accessToken,
        String tokenType
) {
}
