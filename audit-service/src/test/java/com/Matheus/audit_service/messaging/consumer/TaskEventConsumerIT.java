package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.config.PostgresContainerConfig;
import com.Matheus.audit_service.domain.EntityType;
import com.Matheus.audit_service.domain.EventType;
import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.repository.AuditLogRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Java;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(PostgresContainerConfig.class)
@EmbeddedKafka(partitions = 1, topics = "task-created")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskEventConsumerIT {
    @Autowired
    private KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    @Autowired
    private AuditLogRepository repository;

    @Autowired
    private KafkaAdmin kafkaAdmin;


    @Test
    @Order(1)
    @DisplayName("Should consume Kafka message and persist audit log in database")
    void consume_CreatesAuditInDatabase_WhenTaskCreatedEventIsPublished() {
        Long expectedTaskId = 1L;
        String taskTitle = "Study Kafka";

        TaskCreatedEvent event = new TaskCreatedEvent(
                UUID.randomUUID(),
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
            assertEquals(event.eventId(), savedAudit.getEventId());
            assertNotNull(savedAudit.getCreatedAt());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Should create only one audit when same event is published twice")
    void consume_CreatesOnlyOneAudit_WhenSameEventIsPublishedTwice() {
        var eventId = UUID.randomUUID();

        var event = new TaskCreatedEvent(
                eventId,
                10L,
                "Duplicate Task",
                "Descrição",
                "TODO",
                "HIGH");

        kafkaTemplate.send("task-created", event);
        kafkaTemplate.send("task-created", event);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var audits = repository.findAll().stream()
                    .filter(audit -> eventId.equals(audit.getEventId()))
                    .toList();
            assertEquals(1, audits.size());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Should commit offset after acknowledgment is processed")
    void consumeTaskCreated_CommitsOffset_WhenMessageIsAcknowledged() throws Exception {
        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                100L,
                "ACK Test",
                "Testing Kafka acknowledgment",
                "TODO",
                "HIGH");

        var result = kafkaTemplate.send("task-created", event).get();
        var metadata = result.getRecordMetadata();
        var topicPartition = new TopicPartition(metadata.topic(), metadata.partition());
        var expectedOffset = metadata.offset() + 1;

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
                    var committedOffset = getCommittedOffset(topicPartition);
                    assertEquals(expectedOffset, committedOffset);
                });
    }

        private long getCommittedOffset(TopicPartition topicPartition) throws Exception {

            try (var adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

                var offsets = adminClient.listConsumerGroupOffsets("audit-service")
                        .partitionsToOffsetAndMetadata()
                        .get();

                var metadata = offsets.get(topicPartition);

                return metadata == null ? -1 : metadata.offset();
        }
    }
}
