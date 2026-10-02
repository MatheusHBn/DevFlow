package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.config.PostgresContainerConfig;
import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.repository.AuditLogRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(PostgresContainerConfig.class)
@EmbeddedKafka(partitions = 1, topics = "task-created")
@ActiveProfiles("test")
public class TaskEventConsumerIT {
    @Autowired
    private KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    @Autowired
    private AuditLogRepository repository;

    @Test
    @DisplayName("Should consume Kafka message and persist audit log in database")
    void consume_CreatesAuditInDatabase_WhenTaskCreatedEventIsPublished() {
        Long expectedTaskId = 1L;
        String taskTitle = "Study Kafka";

        TaskCreatedEvent event = new TaskCreatedEvent(
                expectedTaskId,
                taskTitle,
                "Descrição da task",
                "PENDING",
                "HIGH");

        kafkaTemplate.send("task-created", event);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var savedAudit = repository.findAll().stream()
                    .filter(audit -> audit.getEntityId().equals(expectedTaskId))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("O registro de Audit ainda não foi salvo no banco"));

            assertEquals(expectedTaskId, savedAudit.getEntityId());
            assertEquals(EventType.TASK_CREATED, savedAudit.getEventType());
            assertEquals(EntityType.TASK, savedAudit.getEntityType());
            assertEquals("Task \"" + taskTitle + "\" was created.", savedAudit.getDescription());
            assertNotNull(savedAudit.getCreatedAt());
        });
    }
}
