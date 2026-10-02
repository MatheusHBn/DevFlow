package com.Matheus.audit_service.service;

import com.Matheus.audit_service.domain.Audit;
import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private AuditService service;

    @Captor
    private ArgumentCaptor<Audit> auditCaptor;

    @Test
    @DisplayName("Should correctly map and save audit log when processing task event")
    void createAuditLog_SavesAudit_WhenTaskCreatedEventReceived() {
        Long expectedTaskId = 1L;
        String taskTitle = "Configurar banco de dados";

        var event = new TaskCreatedEvent(
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
        assertNotNull(savedAudit.getCreatedAt());
    }
}