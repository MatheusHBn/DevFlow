package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.messaging.event.*;
import com.Matheus.audit_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final AuditService service;

    @KafkaListener(topics = "task-created", groupId = "audit-service")
    public void consumeTaskCreated(TaskCreatedEvent event) {
        service.createAuditLog(event);
    }

    @KafkaListener(topics = "task-priority-changed", groupId = "audit-service")
    public void consumeTaskPriorityChanged(TaskPriorityChangedEvent event) {
        service.createPriorityChangeAudit(event);
    }

    @KafkaListener(topics = "task-status-changed", groupId = "audit-service")
    public void consumeTaskStatusChanged(TaskStatusChangedEvent event) {
        service.createStatusChangeAudit(event);
    }

    @KafkaListener(topics = "task-updated", groupId = "audit-service")
    public void consumeTaskUpdated(TaskUpdatedEvent event) {
        service.createUpdateAudit(event);
    }

    @KafkaListener(topics = "task-deleted", groupId = "audit-service")
    public void consumeTaskDeleted(TaskDeletedEvent event) {
        service.createDeleteAudit(event);
    }
}
