package com.Matheus.audit_service.messaging.event;

public record TaskUpdatedEvent(
        Long taskId,
        String title
) {
}
