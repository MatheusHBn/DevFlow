package com.Matheus.audit_service.messaging.event;

public record TaskStatusChangedEvent(
        Long taskId,
        String previousStatus,
        String newStatus
) {
}
