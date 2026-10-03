package com.Matheus.audit_service.messaging.event;

public record TaskDeletedEvent(
        Long taskId,
        String title
) {
}
