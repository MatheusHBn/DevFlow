package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.messaging.event.TaskCreatedEvent;
import com.Matheus.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "task-created", groupId = "notification-service")
    public void consume(TaskCreatedEvent event) {
        notificationService.createNotification(event);
    }
}
