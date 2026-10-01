package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.messaging.event.TaskCreatedEvent;
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
    private NotificationService notificationService;

    @InjectMocks
    private TaskEventConsumer consumer;

    @Test
    @Order(1)
    @DisplayName("Should consume TaskCreatedEvent and delegate to NotificationService")
    void consume_DelegatesToNotificationService_WhenTaskCreatedEventIsReceived() {
        var event = new TaskCreatedEvent(
                1L,
                "Study Kafka",
                "Learn producers and consumers",
                "TODO",
                "MEDIUM");

        consumer.consume(event);

        verify(notificationService, times(1)).createNotification(event);
    }
}