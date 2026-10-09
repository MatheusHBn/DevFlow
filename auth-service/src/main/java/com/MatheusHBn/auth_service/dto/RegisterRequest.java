package com.MatheusHBn.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must have between 3 and 50 characters")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must have between 8 and 72 characters")
        String password
) {
}
