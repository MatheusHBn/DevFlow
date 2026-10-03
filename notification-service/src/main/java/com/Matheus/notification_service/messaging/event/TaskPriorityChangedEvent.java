package com.Matheus.notification_service.messaging.event;

public record TaskPriorityChangedEvent(
        Long taskId,
        String previousPriority,
        String newPriority
) {
}
