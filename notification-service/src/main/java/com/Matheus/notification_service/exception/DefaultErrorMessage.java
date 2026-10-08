package com.Matheus.notification_service.exception;

public record DefaultErrorMessage(
        int status,
        String message
) {
}
