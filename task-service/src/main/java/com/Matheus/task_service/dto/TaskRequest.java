package com.Matheus.task_service.dto;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;

public record TaskRequest(
        String title,
        String description,
        PriorityTask priority,
        StatusTask status,
        Long projectId
) {
}
