package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.messaging.event.*;
import com.Matheus.audit_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final AuditService service;

    @KafkaListener(topics = "task-created", groupId = "audit-service")
    public void consumeTaskCreated(
            TaskCreatedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId,
            Acknowledgment acknowledgment) {

        processEvent(event, correlationId, acknowledgment, () -> service.createAuditLog(event));
    }

    @KafkaListener(topics = "task-priority-changed", groupId = "audit-service")
    public void consumeTaskPriorityChanged(
            TaskPriorityChangedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId,
            Acknowledgment acknowledgment) {
        processEvent(event, correlationId, acknowledgment, () -> service.createPriorityChangeAudit(event));
    }

    @KafkaListener(topics = "task-status-changed", groupId = "audit-service")
    public void consumeTaskStatusChanged(
            TaskStatusChangedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId,
            Acknowledgment acknowledgment) {
        processEvent(event, correlationId, acknowledgment, () -> service.createStatusChangeAudit(event));
    }

    @KafkaListener(topics = "task-updated", groupId = "audit-service")
    public void consumeTaskUpdated(
            TaskUpdatedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId,
            Acknowledgment acknowledgment) {
        processEvent(event, correlationId, acknowledgment, () -> service.createUpdateAudit(event));
    }

    @KafkaListener(topics = "task-deleted", groupId = "audit-service")
    public void consumeTaskDeleted(
            TaskDeletedEvent event,
            @Header(name = "X-Correlation-ID", required = false)
            byte[] correlationId,
            Acknowledgment acknowledgment) {
        processEvent(event, correlationId, acknowledgment, () -> service.createDeleteAudit(event));
    }

    private void processEvent(Object event, byte[] correlationId, Acknowledgment acknowledgment, Runnable processing) {
        try {
            if (correlationId != null && correlationId.length > 0) {
                MDC.put("correlationId", new String(correlationId, StandardCharsets.UTF_8));
            }

            log.info("Received audit event: {}", event.getClass().getSimpleName());

            processing.run();

            log.info("Audit event processed successfully: {}", event.getClass().getSimpleName());

            acknowledgment.acknowledge();

        } catch (RuntimeException exception) {
            log.error("Failed to process audit event: {}", event.getClass().getSimpleName(), exception);
            throw exception;

        } finally {
            MDC.remove("correlationId");
        }
    }
}
