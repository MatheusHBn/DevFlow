package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.messaging.event.*;
import com.Matheus.audit_service.service.AuditService;
import org.springframework.kafka.support.Acknowledgment;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final AuditService service;

    @KafkaListener(topics = "task-created", groupId = "audit-service")
    public void consumeTaskCreated(TaskCreatedEvent event, Acknowledgment acknowledgment) {
        service.createAuditLog(event);

        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "task-priority-changed", groupId = "audit-service")
    public void consumeTaskPriorityChanged(TaskPriorityChangedEvent event, Acknowledgment acknowledgment) {
        service.createPriorityChangeAudit(event);

        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "task-status-changed", groupId = "audit-service")
    public void consumeTaskStatusChanged(TaskStatusChangedEvent event, Acknowledgment acknowledgment) {
        service.createStatusChangeAudit(event);

        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "task-updated", groupId = "audit-service")
    public void consumeTaskUpdated(TaskUpdatedEvent event, Acknowledgment acknowledgment) {
        service.createUpdateAudit(event);

        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "task-deleted", groupId = "audit-service")
    public void consumeTaskDeleted(TaskDeletedEvent event, Acknowledgment acknowledgment) {
        service.createDeleteAudit(event);

        acknowledgment.acknowledge();
    }
}
