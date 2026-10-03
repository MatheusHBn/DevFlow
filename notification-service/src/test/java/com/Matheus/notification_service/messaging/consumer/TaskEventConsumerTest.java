package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.messaging.event.*;
import com.Matheus.notification_service.service.NotificationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskEventConsumerTest {
    @Mock
    private NotificationService service;

    @InjectMocks
    private TaskEventConsumer consumer;

    @Test
    @Order(1)
    @DisplayName("Should consume TaskCreatedEvent and delegate to NotificationService")
    void consumeTaskCreated_DelegatesToNotificationService_WhenTaskCreatedEventIsReceived() {
        var event = new TaskCreatedEvent(
                1L,
                "Study Kafka",
                "Learn producers and consumers",
                "TODO",
                "MEDIUM");

        consumer.consumeTaskCreated(event);

        verify(service, times(1)).createNotification(event);
    }

    @Test
    @Order(2)
    @DisplayName("Should call notification service when task status is changed")
    void consumeTaskStatusChanged_CallsNotificationService() {
        var event = new TaskStatusChangedEvent(
                8L,
                "DONE",
                "IN_PROGRESS");

        consumer.consumeTaskStatusChanged(event);

        verify(service).createStatusChangeNotification(event);
    }

    @Test
    @Order(3)
    @DisplayName("Should call notification service when task priority is changed")
    void consumeTaskPriorityChanged_CallsNotificationService() {
        var event = new TaskPriorityChangedEvent(
                8L,
                "ULTRA",
                "LOW");

        consumer.consumeTaskPriorityChanged(event);

        verify(service).createPriorityChangeNotification(event);
    }

    @Test
    @Order(4)
    @DisplayName("Should call notification service when task is updated")
    void consumeTaskUpdated_CallsNotificationService() {
        var event = new TaskUpdatedEvent(
                7L,
                "Estudar Kafka Avançado");

        consumer.consumeTaskUpdated(event);

        verify(service).createUpdateNotification(event);
    }

    @Test
    @Order(5)
    @DisplayName("Should call notification service when task is deleted")
    void consumeTaskDeleted_CallsNotificationService() {
        var event = new TaskDeletedEvent(
                6L,
                "Test notification");

        consumer.consumeTaskDeleted(event);

        verify(service).createDeleteNotification(event);
    }
}