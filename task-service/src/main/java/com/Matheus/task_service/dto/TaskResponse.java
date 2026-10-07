package com.Matheus.task_service.dto;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TaskResponse(
        @Schema(description = "Unique identifier of the task", example = "1")
        Long id,

        @Schema(description = "Title of the task", example = "Implement JWT authentication")
        String title,

        @Schema(description = "Detailed description of the task",
                example = "Implement JWT authentication using Spring Security")
        String description,

        @Schema(description = "Current status of the task", example = "IN_PROGRESS")
        StatusTask status,

        @Schema(description = "Current priority of the task", example = "HIGH")
        PriorityTask priority,

        @Schema(description = "Deadline for completing the task", example = "2026-10-15T18:00:00")
        LocalDateTime dueDate,

        @Schema(description = "Date and time when the task was created", example = "2026-10-07T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Date and time when the task was last updated", example = "2026-10-08T16:45:00")
        LocalDateTime updatedAt
) {
}
