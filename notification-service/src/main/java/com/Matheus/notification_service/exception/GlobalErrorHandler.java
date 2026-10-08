package com.Matheus.notification_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NotificationNotFound.class)
    public ResponseEntity<DefaultErrorMessage> handleNotificationNotFound(NotificationNotFound e) {
        var defaultErrorMessage = new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorMessage);
    }
}
