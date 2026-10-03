package com.Matheus.audit_service.messaging.event;

public record TaskPriorityChangedEvent(
        Long taskId,
        String previousPriority,
        String newPriority
) {
}
