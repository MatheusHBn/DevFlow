package com.Matheus.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Notification data returned by the API")
public record NotificationResponse(
        @Schema(description = "Unique identifier of the notification", example = "1")
        Long id,

        @Schema(description = "Identifier of the related task", example = "42")
        Long taskId,

        @Schema(description = "Notification message", example = "Task \"Implement JWT authentication\" was created.")
        String message,

        @Schema(description = "Indicates whether the notification has been read", example = "false")
        boolean read,

        @Schema(description = "Date and time when the notification was created", example = "2026-10-07T18:30:00")
        LocalDateTime createdAt

) {
}
