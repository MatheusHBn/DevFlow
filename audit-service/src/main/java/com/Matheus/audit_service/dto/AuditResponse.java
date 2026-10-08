package com.Matheus.audit_service.dto;

import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Audit log data returned by the API")
public record AuditResponse(

        @Schema(description = "Unique identifier of the audit log", example = "1")
        Long id,

        @Schema(description = "Unique identifier of the processed event", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID eventId,

        @Schema(description = "Type of event that generated the audit log", example = "TASK_CREATED")
        EventType eventType,

        @Schema(description = "Type of entity affected by the event", example = "TASK")
        EntityType entityType,

        @Schema(description = "Identifier of the affected entity", example = "42")
        Long entityId,

        @Schema(description = "Description of the action performed",
                example = "Task \"Implement JWT authentication\" was created.")
        String description,

        @Schema(description = "Date and time when the audit log was created", example = "2026-10-07T18:30:00")
        LocalDateTime createdAt
) {}
