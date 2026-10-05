package com.Matheus.task_service.messaging.event;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;

import java.util.UUID;

public record TaskCreatedEvent(
        UUID eventId,
        Long taskId,
        String title,
        String description,
        StatusTask status,
        PriorityTask priority
) {
}
