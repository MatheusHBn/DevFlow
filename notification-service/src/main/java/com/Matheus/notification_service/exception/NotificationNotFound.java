package com.Matheus.notification_service.exception;

public class NotificationNotFound extends RuntimeException {
    public NotificationNotFound(String message) {
        super(message);
    }
}
