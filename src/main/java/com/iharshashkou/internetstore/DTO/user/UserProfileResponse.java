package com.iharshashkou.internetstore.DTO.user;

public record UserProfileResponse(
        String name,
        String email,
        Role role
) {
}