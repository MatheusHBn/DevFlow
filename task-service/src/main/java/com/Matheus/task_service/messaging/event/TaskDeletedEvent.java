package com.Matheus.task_service.messaging.event;

public record TaskDeletedEvent(
        Long taskId,
        String title
) {
}
