package com.Matheus.audit_service.messaging.event;

import java.util.UUID;

public record TaskStatusChangedEvent(
        UUID eventId,
        Long taskId,
        String previousStatus,
        String newStatus
) {
}
