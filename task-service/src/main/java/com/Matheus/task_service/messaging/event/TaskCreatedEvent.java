package com.Matheus.task_service.messaging.event;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;

public record TaskCreatedEvent(
        Long taskId,
        String title,
        String description,
        StatusTask status,
        PriorityTask priority
) {
}
