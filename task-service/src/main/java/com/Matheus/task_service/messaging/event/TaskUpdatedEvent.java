package com.Matheus.task_service.messaging.event;

public record TaskUpdatedEvent(
        Long taskId,
        String title
) {
}
