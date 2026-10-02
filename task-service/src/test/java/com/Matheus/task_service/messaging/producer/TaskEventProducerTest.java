package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.messaging.event.TaskCreatedEvent;
import com.Matheus.task_service.messaging.event.TaskStatusChangedEvent;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

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
                1L,
                "Study Kafka",
                "Learn producers and consumers",
                StatusTask.TODO,
                PriorityTask.MEDIUM);

        producer.publishTaskCreated(event);

        verify(kafkaTemplate).send("task-created", event);
    }

    @Test
    @Order(2)
    @DisplayName("Should publish task status changed event to the correct topic")
    void publishTaskStatusChanged_SendsEventToCorrectTopic() {
        var event = new TaskStatusChangedEvent(
                1L,
                "TODO",
                "DONE");

        producer.publishTaskStatusChanged(event);

        verify(kafkaTemplate).send("task-status-changed", event);
    }
}