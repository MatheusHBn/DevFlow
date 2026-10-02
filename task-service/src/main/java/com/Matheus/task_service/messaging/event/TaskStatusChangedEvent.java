package com.Matheus.task_service.messaging.event;

public record TaskStatusChangedEvent(
        Long taskId,
        String previousStatus,
        String newStatus
) {
}
