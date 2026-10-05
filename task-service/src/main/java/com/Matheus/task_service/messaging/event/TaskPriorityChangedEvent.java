package com.Matheus.task_service.messaging.event;

import java.util.UUID;

public record TaskPriorityChangedEvent(
        UUID eventId,
        Long taskId,
        String previousPriority,
        String newPriority
) {
}
