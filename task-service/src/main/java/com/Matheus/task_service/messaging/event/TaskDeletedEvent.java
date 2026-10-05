package com.Matheus.task_service.messaging.event;

import java.util.UUID;

public record TaskDeletedEvent(
        UUID eventId,
        Long taskId,
        String title
) {
}
