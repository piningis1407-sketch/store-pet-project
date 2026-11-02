package com.iharshashkou.internetstore.DTO;

public record UserProfileResponse(
        String name,
        String email,
        Role role
) {
}