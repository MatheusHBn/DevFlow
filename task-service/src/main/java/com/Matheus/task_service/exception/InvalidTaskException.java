package com.Matheus.task_service.exception;

public class InvalidTaskException extends RuntimeException {
    public InvalidTaskException(String message) {
        super(message);
    }
}
