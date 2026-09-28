package com.Matheus.task_service.dto;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        StatusTask status,
        PriorityTask priority,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
