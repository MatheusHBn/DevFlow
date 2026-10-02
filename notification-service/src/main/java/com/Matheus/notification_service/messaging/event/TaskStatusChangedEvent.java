package com.Matheus.notification_service.messaging.event;

public record TaskStatusChangedEvent(
        Long taskId,
        String previousStatus,
        String newStatus
) {
}
