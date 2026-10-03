package com.Matheus.notification_service.messaging.event;

public record TaskDeletedEvent(
        Long taskId,
        String title
) {
}
