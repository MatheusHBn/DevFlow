package com.Matheus.audit_service.exception;

public record DefaultErrorMessage(
        int status,
        String message
) {
}
