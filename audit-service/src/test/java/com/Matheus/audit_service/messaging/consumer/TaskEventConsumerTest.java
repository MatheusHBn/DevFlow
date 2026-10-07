package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.service.AuditService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskEventConsumerTest {

    @Mock
    private AuditService service;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private TaskEventConsumer taskEventConsumer;

    @Test
    @DisplayName("Should delegate the received event to the AuditService")
    @Order(1)
    void consumeTaskCreated_DelegatesToAuditService_WhenTaskCreatedEventIsReceived() {
        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                1L,
                "Configurar banco de dados",
                "Descrição da task",
                "PENDING",
                "HIGH");

        taskEventConsumer.consumeTaskCreated(event, acknowledgment);

        verify(service).createAuditLog(event);
        verify(acknowledgment).acknowledge();
    }

    @Test
    @Order(2)
    @DisplayName("Should not acknowledge message when processing fails")
    void consumeTaskCreated_DoesNotAcknowledge_WhenProcessingFails() {

        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                1L,
                "Configurar banco de dados",
                "Descrição da task",
                "PENDING",
                "HIGH");

        doThrow(new RuntimeException("Database error")).when(service).createAuditLog(event);

        assertThrows(RuntimeException.class, () -> taskEventConsumer.consumeTaskCreated(event, acknowledgment));

        verify(acknowledgment, never()).acknowledge();
    }
}