package com.iharshashkou.internetstore.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Name is required")
        @Size(max=30,message="The name must not exceed 30 characters")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "The password must contain at least 8 characters ")
        String password


) {
}
