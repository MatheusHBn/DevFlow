package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.messaging.event.*;
import com.Matheus.notification_service.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

public class TaskEventConsumer {

    private final NotificationService service;

    public TaskEventConsumer(NotificationService notificationService) {
        this.service = notificationService;
    }

    @KafkaListener(topics = "task-created", groupId = "notification-service")
    public void consumeTaskCreated(TaskCreatedEvent event) {
        service.createNotification(event);
    }

    @KafkaListener(topics = "task-status-changed", groupId = "notification-service")
    public void consumeTaskStatusChanged(TaskStatusChangedEvent event) {
        service.createStatusChangeNotification(event);
    }

    @KafkaListener(topics = "task-priority-changed", groupId = "notification-service")
    public void consumeTaskPriorityChanged(TaskPriorityChangedEvent event) {
        service.createPriorityChangeNotification(event);
    }

    @KafkaListener(topics = "task-updated", groupId = "notification-service")
    public void consumeTaskUpdated(TaskUpdatedEvent event) {
        service.createUpdateNotification(event);
    }

    @KafkaListener(topics = "task-deleted", groupId = "notification-service")
    public void consumeTaskDeleted(TaskDeletedEvent event) {
        service.createDeleteNotification(event);
    }
}
