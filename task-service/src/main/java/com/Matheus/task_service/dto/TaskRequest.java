package com.Matheus.task_service.dto;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder

@Schema(description = "Data required to create a new task")
public record TaskRequest(
        @Schema(description = "Title of the task",
                example = "Implement JWT authentication",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String title,

        @Schema(description = "Detailed description of the task",
                example = "Implement authentication and authorization using Spring Security and JWT",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String description,

        @Schema(description = "Initial status of the task",
                example = "TODO",
                defaultValue = "TODO")
        StatusTask status,

        @Schema(description = "Priority level of the task", example = "HIGH")
        PriorityTask priority,

        @Schema(description = "Deadline for completing the task", example = "2026-10-15T18:00:00")
        LocalDateTime dueDate
) {
}
