package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final AuditService auditService;

    @KafkaListener(topics = "task-created", groupId = "audit-service")
    public void consume(TaskCreatedEvent event) {
        auditService.createAuditLog(event);
    }
}
