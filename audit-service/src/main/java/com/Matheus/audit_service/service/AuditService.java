package com.Matheus.audit_service.service;

import com.Matheus.audit_service.domain.Audit;
import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import com.Matheus.audit_service.messaging.event.*;
import com.Matheus.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    public void createAuditLog(TaskCreatedEvent event) {

        if (repository.existsByEventId(event.eventId())) {
            return;
        }

        var audit = Audit.builder()
                .eventId(event.eventId())
                .eventType(EventType.TASK_CREATED)
                .entityType(EntityType.TASK)
                .entityId(event.taskId())
                .description("Task \"%s\" was created.".formatted(event.title()))
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(audit);
    }

    public void createStatusChangeAudit(TaskStatusChangedEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            return;
        }

        var audit = Audit.builder()
                .eventId(event.eventId())
                .eventType(EventType.TASK_STATUS_CHANGED)
                .entityType(EntityType.TASK)
                .entityId(event.taskId())
                .description("Task " + event.taskId()
                                + " changed status from " + event.previousStatus()
                                + " to " + event.newStatus() + ".")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(audit);
    }

    public void createPriorityChangeAudit(TaskPriorityChangedEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            return;
        }

        var audit = Audit.builder()
                .eventId(event.eventId())
                .eventType(EventType.TASK_PRIORITY_CHANGED)
                .entityType(EntityType.TASK)
                .entityId(event.taskId())
                .description("Task " + event.taskId()
                                + " changed priority from " + event.previousPriority()
                                + " to " + event.newPriority() + ".")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(audit);
    }

    public void createUpdateAudit(TaskUpdatedEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            return;
        }

        var audit = Audit.builder()
                .eventId(event.eventId())
                .eventType(EventType.TASK_UPDATED)
                .entityType(EntityType.TASK)
                .entityId(event.taskId())
                .description("Task \"%s\" was updated.".formatted(event.title()))
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(audit);
    }

    public void createDeleteAudit(TaskDeletedEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            return;
        }

        var audit = Audit.builder()
                .eventId(event.eventId())
                .eventType(EventType.TASK_DELETED)
                .entityType(EntityType.TASK)
                .entityId(event.taskId())
                .description("Task \"%s\" was deleted.".formatted(event.title()))
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(audit);
    }
}
