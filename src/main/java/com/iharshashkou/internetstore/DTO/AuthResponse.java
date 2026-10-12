package com.iharshashkou.internetstore.DTO;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
