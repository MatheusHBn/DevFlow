package com.Matheus.task_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(TaskNotFound.class)
    public ResponseEntity<DefaultErrorMessage> handleTaskNotFound(TaskNotFound e) {
        var defaultErrorMessage = new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorMessage);
    }

    @ExceptionHandler(InvalidTaskException.class)
    public ResponseEntity<DefaultErrorMessage> handleInvalidTaskException(InvalidTaskException e) {
        var defaultErrorMessage = new DefaultErrorMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorMessage);
    }
}
