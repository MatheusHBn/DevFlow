package com.Matheus.notification_service.messaging.event;

public record TaskCreatedEvent(
        Long taskId,
        String title,
        String description,
        String status,
        String priority
) {
}
