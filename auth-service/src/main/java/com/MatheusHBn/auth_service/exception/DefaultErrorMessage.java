package com.MatheusHBn.auth_service.exception;

public record DefaultErrorMessage(
        int status,
        String message
) {
}
