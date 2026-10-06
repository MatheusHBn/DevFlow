package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.messaging.event.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TaskEventProducer producer;

    @Test
    @DisplayName("Should publish TaskCreatedEvent to Kafka topic when successful")
    @Order(1)
    void publishTaskCreated_PublishesEvent_WhenSuccessful() {
        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                1L,
                "Study Kafka",
                "Learn producers and consumers",
                StatusTask.TODO,
                PriorityTask.MEDIUM);

        producer.publishTaskCreated(event);

        verify(kafkaTemplate).send("task-created", "1", event);
    }

    @Test
    @Order(2)
    @DisplayName("Should publish task status changed event to the correct topic")
    void publishTaskStatusChanged_SendsEventToCorrectTopic() {
        var event = new TaskStatusChangedEvent(
                UUID.randomUUID(),
                1L,
                "TODO",
                "DONE");

        producer.publishTaskStatusChanged(event);

        verify(kafkaTemplate).send("task-status-changed", "1", event);
    }

    @Test
    @Order(3)
    @DisplayName("Should publish TaskPriorityChangedEvent to 'task-priority-changed' topic")
    void publishTaskPriorityChanged_SendsEventToCorrectTopic() {

        var event = new TaskPriorityChangedEvent( UUID.randomUUID(),8L, "HIGH", "ULTRA");

        producer.publishTaskPriorityChanged(event);

        verify(kafkaTemplate).send("task-priority-changed", "8", event);
    }

    @Test
    @Order(4)
    @DisplayName("Should publish TaskUpdatedEvent to 'task-updated' topic")
    void publishTaskUpdated_SendsEventToCorrectTopic() {

        var event = new TaskUpdatedEvent(UUID.randomUUID(),1L, "Novo Titulo");

        producer.publishTaskUpdated(event);

        verify(kafkaTemplate).send("task-updated", "1", event);
    }

    @Test
    @Order(5)
    @DisplayName("Should publish TaskDeletedEvent to 'task-deleted' topic")
    void publishTaskDeleted_SendsEventToCorrectTopic() {
        var event = new TaskDeletedEvent(UUID.randomUUID(),1L, "Um novo título");

        producer.publishTaskDeleted(event);

        verify(kafkaTemplate).send("task-deleted", "1", event);
    }
}