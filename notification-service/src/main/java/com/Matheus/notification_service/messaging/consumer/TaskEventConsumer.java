package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.messaging.event.*;
import com.Matheus.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventConsumer.class);

    private final NotificationService service;

    @KafkaListener(topics = "task-created", groupId = "notification-service")
    public void consumeTaskCreated(
            TaskCreatedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId) {
        processEvent(correlationId, () -> {
            log.info("Received task-created event for taskId={}", event.taskId());
            service.createNotification(event);
        });
    }

    @KafkaListener(topics = "task-status-changed", groupId = "notification-service")
    public void consumeTaskStatusChanged(
            TaskStatusChangedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId) {
        processEvent(correlationId, () -> {
            log.info("Received task-status-changed event for taskId={}", event.taskId());
            service.createStatusChangeNotification(event);
        });
    }

    @KafkaListener(topics = "task-priority-changed", groupId = "notification-service")
    public void consumeTaskPriorityChanged(
            TaskPriorityChangedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId) {
        processEvent(correlationId, () -> {
            log.info("Received task-priority-changed event for taskId={}", event.taskId());
            service.createPriorityChangeNotification(event);
        });
    }

    @KafkaListener(topics = "task-updated", groupId = "notification-service")
    public void consumeTaskUpdated(
            TaskUpdatedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId) {
        processEvent(correlationId, () -> {
            log.info("Received task-updated event for taskId={}", event.taskId());
            service.createUpdateNotification(event);
        });
    }

    @KafkaListener(topics = "task-deleted", groupId = "notification-service")
    public void consumeTaskDeleted(
            TaskDeletedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId) {
        processEvent(correlationId, () -> {
            log.info("Received task-deleted event for taskId={}", event.taskId());
            service.createDeleteNotification(event);
        });
    }

    private void processEvent(byte[] correlationId, Runnable processing) {
        try {
            if (correlationId != null && correlationId.length > 0) {
                MDC.put("correlationId", new String(correlationId, StandardCharsets.UTF_8));
            }
            processing.run();
        } finally {
            MDC.remove("correlationId");
        }
    }
}
