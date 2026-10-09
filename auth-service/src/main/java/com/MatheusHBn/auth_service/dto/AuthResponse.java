package com.MatheusHBn.auth_service.dto;

public record AuthResponse(
        Long id,
        String username,
        String email
) {
}
