package com.Matheus.notification_service.api;

import com.Matheus.notification_service.dto.NotificationResponse;
import com.Matheus.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for retrieving task notifications")
public class NotificationController {

    private final NotificationService service;

    @Operation(summary = "Get all notifications")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> findAllNotifications() {
        return ResponseEntity.ok(service.findAllNotifications());
    }

    @Operation(summary = "Get a notification by ID")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> findNotificationById(
            @Parameter(description = "Unique identifier of the notification", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findNotificationById(id));
    }

    @Operation(summary = "Mark a notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @Parameter(description = "Unique identifier of the notification", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }
}
