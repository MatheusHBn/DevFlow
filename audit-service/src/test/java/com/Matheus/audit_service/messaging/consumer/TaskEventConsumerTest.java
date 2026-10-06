package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.service.AuditService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskEventConsumerTest {

    @Mock
    private AuditService service;

    @InjectMocks
    private TaskEventConsumer taskEventConsumer;

    @Test
    @DisplayName("Should delegate the received event to the AuditService")
    void consumeTaskCreated_DelegatesToAuditService_WhenTaskCreatedEventIsReceived() {

        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                1L,
                "Configurar banco de dados",
                "Descrição da task",
                "PENDING",
                "HIGH");

        taskEventConsumer.consumeTaskCreated(event);

        verify(service).createAuditLog(event);
    }
}