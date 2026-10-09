package com.MatheusHBn.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<DefaultErrorMessage> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        var defaultErrorMessage = new DefaultErrorMessage(HttpStatus.CONFLICT.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(defaultErrorMessage);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<DefaultErrorMessage> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        var defaultErrorMessage = new DefaultErrorMessage(HttpStatus.CONFLICT.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(defaultErrorMessage);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultErrorMessage> handleBadCredentialsException() {

        var error = new DefaultErrorMessage(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
