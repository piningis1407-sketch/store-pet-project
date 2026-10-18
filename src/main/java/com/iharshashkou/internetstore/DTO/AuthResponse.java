package com.iharshashkou.internetstore.DTO;

public record AuthResponse(
        String accessToken,
        String tokenType
) {
}
