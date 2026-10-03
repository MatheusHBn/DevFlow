package com.Matheus.notification_service.messaging.event;

public record TaskUpdatedEvent(
        Long taskId,
        String title
) {
}
