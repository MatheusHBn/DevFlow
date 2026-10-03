package com.Matheus.notification_service.service;

import com.Matheus.notification_service.domain.Notification;
import com.Matheus.notification_service.messaging.event.*;
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
    private ArgumentCaptor<Notification> captor;

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

        verify(repository).save(captor.capture());

        Notification capturedNotification = captor.getValue();

        assertEquals(1L, capturedNotification.getTaskId());
        assertEquals("Task \"Study Kafka\" was created.", capturedNotification.getMessage());
        assertFalse(capturedNotification.isRead());
        assertNotNull(capturedNotification.getCreatedAt());
    }

    @Test
    @Order(2)
    @DisplayName("Should create notification when task status is changed")
    void createStatusChangeNotification_CreatesNotification_WhenSuccessful() {
        var event = new TaskStatusChangedEvent(
                8L,
                "DONE",
                "IN_PROGRESS");

        service.createStatusChangeNotification(event);

        verify(repository).save(captor.capture());

        var notification = captor.getValue();

        assertEquals(8L, notification.getTaskId());
        assertEquals("Task 8 changed status from DONE to IN_PROGRESS.", notification.getMessage());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    @Order(3)
    @DisplayName("Should create notification when task priority is changed")
    void createPriorityChangeNotification_CreatesNotification_WhenSuccessful() {
        var event = new TaskPriorityChangedEvent(
                8L,
                "ULTRA",
                "LOW");

        service.createPriorityChangeNotification(event);

        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());

        var notification = captor.getValue();

        assertEquals(8L, notification.getTaskId());
        assertEquals("Task 8 changed priority from ULTRA to LOW.", notification.getMessage());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    @Order(4)
    @DisplayName("Should create notification when task is updated")
    void createUpdateNotification_CreatesNotification_WhenSuccessful() {
        var event = new TaskUpdatedEvent(
                7L,
                "Estudar Kafka Avançado");

        service.createUpdateNotification(event);


        verify(repository).save(captor.capture());

        var notification = captor.getValue();

        assertEquals(7L, notification.getTaskId());
        assertEquals("Task \"Estudar Kafka Avançado\" was updated.", notification.getMessage());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    @Order(5)
    @DisplayName("Should create notification when task is deleted")
    void createDeleteNotification_CreatesNotification_WhenSuccessful() {
        var event = new TaskDeletedEvent(
                6L,
                "Test notification");

        service.createDeleteNotification(event);

        verify(repository).save(captor.capture());

        var notification = captor.getValue();

        assertEquals(6L, notification.getTaskId());
        assertEquals("Task \"Test notification\" was deleted.", notification.getMessage());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }
}