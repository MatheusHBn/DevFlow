package com.Matheus.audit_service.messaging.event;

import java.util.UUID;

public record TaskCreatedEvent(
        UUID eventId,
        Long taskId,
        String title,
        String description,
        String status,
        String priority
) {
}
