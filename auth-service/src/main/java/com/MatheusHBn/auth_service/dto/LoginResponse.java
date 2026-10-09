package com.MatheusHBn.auth_service.dto;

public record LoginResponse(
        Long id,
        String username,
        String email,
        String token
) {
}
