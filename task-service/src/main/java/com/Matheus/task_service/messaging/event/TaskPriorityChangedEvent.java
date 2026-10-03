package com.Matheus.task_service.messaging.event;

public record TaskPriorityChangedEvent(
        Long taskId,
        String previousPriority,
        String newPriority
) {
}
