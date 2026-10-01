package com.Matheus.audit_service.service;

import com.Matheus.audit_service.domain.Audit;
import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    public void createAuditLog(TaskCreatedEvent event) {

        var audit = Audit.builder()
                .eventType(EventType.TASK_CREATED)
                .entityType(EntityType.TASK)
                .entityId(event.taskId())
                .description("Task \"%s\" was created.".formatted(event.title()))
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(audit);
    }
}
