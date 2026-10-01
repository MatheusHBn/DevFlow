package com.Matheus.notification_service.service;

import com.Matheus.notification_service.domain.Notification;
import com.Matheus.notification_service.messaging.event.TaskCreatedEvent;
import com.Matheus.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Test
    @Order(1)
    @DisplayName("Should create and save a new notification when TaskCreatedEvent is received")
    void createNotification_SavesNotification_WhenTaskCreatedEventReceived() {
        var event = new TaskCreatedEvent(
                1L,
                "Study Kafka",
                "Learn producers and consumers",
                "TODO",
                "MEDIUM"
        );

        service.createNotification(event);

        verify(repository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();

        assertEquals(1L, capturedNotification.getTaskId());
        assertEquals("Task \"Study Kafka\" was created.", capturedNotification.getMessage());
        assertFalse(capturedNotification.isRead());
        assertNotNull(capturedNotification.getCreatedAt());
    }
}