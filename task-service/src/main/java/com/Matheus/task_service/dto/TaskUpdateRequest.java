package com.Matheus.task_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskUpdateRequest(
        String title,
        String description,
        LocalDateTime dueDate,
        Long projectId

) {
}
