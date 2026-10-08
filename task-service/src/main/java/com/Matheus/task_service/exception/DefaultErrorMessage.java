package com.Matheus.task_service.exception;

public record DefaultErrorMessage(
        int status,
        String message
) {
}
