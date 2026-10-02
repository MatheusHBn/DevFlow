package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.messaging.event.TaskCreatedEvent;
import com.Matheus.notification_service.messaging.event.TaskStatusChangedEvent;
import com.Matheus.notification_service.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

public class TaskEventConsumer {

    private final NotificationService notificationService;

    public TaskEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "task-created", groupId = "notification-service")
    public void consumeTaskCreated(TaskCreatedEvent event) {
        notificationService.createNotification(event);
    }

    @KafkaListener(topics = "task-status-changed", groupId = "notification-service")
    public void consumeTaskStatusChanged(TaskStatusChangedEvent event) {
        notificationService.createStatusChangeNotification(event);
    }
}
