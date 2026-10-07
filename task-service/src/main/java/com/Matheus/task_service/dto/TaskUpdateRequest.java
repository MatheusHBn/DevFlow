package com.Matheus.task_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Data used to update an existing task")
public record TaskUpdateRequest(
        @Schema(description = "Updated title of the task",
                example = "Implement JWT authentication",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String title,

        @Schema(description = "Updated description of the task",
                example = "Implement JWT authentication using Spring Security",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String description,

        @Schema(description = "Updated deadline for the task", example = "2026-10-20T18:00:00")
        LocalDateTime dueDate
) {
}
