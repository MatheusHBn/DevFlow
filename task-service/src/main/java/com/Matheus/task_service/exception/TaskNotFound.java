package com.Matheus.task_service.exception;

public class TaskNotFound extends RuntimeException {

    public TaskNotFound(String message) {
        super(message);
    }
}
