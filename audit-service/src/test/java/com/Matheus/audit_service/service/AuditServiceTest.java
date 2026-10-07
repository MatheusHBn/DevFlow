package com.Matheus.audit_service.service;

import com.Matheus.audit_service.domain.Audit;
import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import com.Matheus.audit_service.messaging.event.*;
import com.Matheus.audit_service.repository.AuditLogRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private AuditService service;

    @Captor
    private ArgumentCaptor<Audit> auditCaptor;

    @Test
    @Order(1)
    @DisplayName("Should correctly map and save audit log when processing task event")
    void createAuditLog_SavesAudit_WhenTaskCreatedEventReceived() {
        Long expectedTaskId = 1L;
        String taskTitle = "Configurar banco de dados";

        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                expectedTaskId,
                taskTitle,
                "Descrição da task",
                "PENDING",
                "HIGH");

        service.createAuditLog(event);

        verify(repository).save(auditCaptor.capture());
        var savedAudit = auditCaptor.getValue();

        assertEquals(EventType.TASK_CREATED, savedAudit.getEventType());
        assertEquals(EntityType.TASK, savedAudit.getEntityType());
        assertEquals(expectedTaskId, savedAudit.getEntityId());
        assertEquals("Task \"" + taskTitle + "\" was created.", savedAudit.getDescription());
        assertNotNull(event.eventId());
        assertNotNull(savedAudit.getCreatedAt());
    }

    @Test
    @Order(2)
    @DisplayName("Should correctly map and save audit log when TaskStatusChangedEvent is received")
    void createStatusChangeAudit_SavesAudit_WhenTaskStatusChangedEventReceived() {
        var event = new TaskStatusChangedEvent(UUID.randomUUID(),2L, "TODO", "DONE");

        service.createStatusChangeAudit(event);

        verify(repository).save(auditCaptor.capture());
        var savedAudit = auditCaptor.getValue();

        assertEquals(EventType.TASK_STATUS_CHANGED, savedAudit.getEventType());
        assertEquals(EntityType.TASK, savedAudit.getEntityType());
        assertEquals(2L, savedAudit.getEntityId());
        assertEquals("Task 2 changed status from TODO to DONE.", savedAudit.getDescription());
        assertNotNull(event.eventId());
        assertNotNull(savedAudit.getCreatedAt());
    }

    @Test
    @Order(3)
    @DisplayName("Should correctly map and save audit log when TaskPriorityChangedEvent is received")
    void createPriorityChangeAudit_SavesAudit_WhenTaskPriorityChangedEventReceived() {
        var event = new TaskPriorityChangedEvent(UUID.randomUUID(),3L, "LOW", "HIGH");

        service.createPriorityChangeAudit(event);

        verify(repository).save(auditCaptor.capture());
        var savedAudit = auditCaptor.getValue();

        assertEquals(EventType.TASK_PRIORITY_CHANGED, savedAudit.getEventType());
        assertEquals(EntityType.TASK, savedAudit.getEntityType());
        assertEquals(3L, savedAudit.getEntityId());
        assertEquals("Task 3 changed priority from LOW to HIGH.", savedAudit.getDescription());
        assertNotNull(event.eventId());
        assertNotNull(savedAudit.getCreatedAt());
    }

    @Test
    @Order(4)
    @DisplayName("Should correctly map and save audit log when TaskUpdatedEvent is received")
    void createUpdateAudit_SavesAudit_WhenTaskUpdatedEventReceived() {
        var event = new TaskUpdatedEvent(UUID.randomUUID(), 4L, "Título Atualizado");

        service.createUpdateAudit(event);

        verify(repository).save(auditCaptor.capture());
        var savedAudit = auditCaptor.getValue();

        assertEquals(EventType.TASK_UPDATED, savedAudit.getEventType());
        assertEquals(EntityType.TASK, savedAudit.getEntityType());
        assertEquals(4L, savedAudit.getEntityId());
        assertEquals("Task \"Título Atualizado\" was updated.", savedAudit.getDescription());
        assertNotNull(event.eventId());
        assertNotNull(savedAudit.getCreatedAt());
    }

    @Test
    @Order(5)
    @DisplayName("Should correctly map and save audit log when TaskDeletedEvent is received")
    void createDeleteAudit_SavesAudit_WhenTaskDeletedEventReceived() {
        var event = new TaskDeletedEvent(UUID.randomUUID(),5L, "Título Deletado");

        service.createDeleteAudit(event);

        verify(repository).save(auditCaptor.capture());
        var savedAudit = auditCaptor.getValue();

        assertEquals(EventType.TASK_DELETED, savedAudit.getEventType());
        assertEquals(EntityType.TASK, savedAudit.getEntityType());
        assertEquals(5L, savedAudit.getEntityId());
        assertEquals("Task \"Título Deletado\" was deleted.", savedAudit.getDescription());
        assertNotNull(event.eventId());
        assertNotNull(savedAudit.getCreatedAt());
    }

    @Test
    @Order(6)
    @DisplayName("Should ignore event when it was already processed")
    void shouldIgnoreEvent_WhenEventWasAlreadyProcessed() {
        var eventId = UUID.randomUUID();

        var event = new TaskCreatedEvent(
                eventId,
                1L,
                "Configurar banco",
                "Descrição",
                "PENDING",
                "HIGH"
        );

        when(repository.existsByEventId(eventId)).thenReturn(true);

        service.createAuditLog(event);

        assertNotNull(event.eventId());
        verify(repository, never()).save(any(Audit.class));
    }
}