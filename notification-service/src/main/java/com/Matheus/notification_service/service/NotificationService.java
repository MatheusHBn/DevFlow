package com.Matheus.notification_service.service;

import com.Matheus.notification_service.domain.Notification;
import com.Matheus.notification_service.dto.NotificationResponse;
import com.Matheus.notification_service.exception.NotificationNotFound;
import com.Matheus.notification_service.mapper.NotificationMapper;
import com.Matheus.notification_service.messaging.event.*;
import com.Matheus.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    public void createNotification(TaskCreatedEvent event) {

        var notification = Notification.builder()
                .taskId(event.taskId())
                .message("Task \"%s\" was created.".formatted(event.title()))
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

    public void createStatusChangeNotification(TaskStatusChangedEvent event) {
        var notification = Notification.builder()
                .taskId(event.taskId())
                .message("Task " + event.taskId()
                        + " changed status from " + event.previousStatus()
                        + " to " + event.newStatus() + ".")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

    public void createPriorityChangeNotification(TaskPriorityChangedEvent event) {
        var notification = Notification.builder()
                .taskId(event.taskId())
                .message(
                        "Task " + event.taskId()
                                + " changed priority from " + event.previousPriority()
                                + " to " + event.newPriority() + "."
                )
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

    public void createUpdateNotification(TaskUpdatedEvent event) {
        var notification = Notification.builder()
                .taskId(event.taskId())
                .message("Task \"" + event.title() + "\" was updated.")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

    public void createDeleteNotification(TaskDeletedEvent event) {
        var notification = Notification.builder()
                .taskId(event.taskId())
                .message("Task \" + event.title() + \" was deleted.")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

    public List<NotificationResponse> findAllNotifications() {
        return mapper.toResponseList(repository.findAll());
    }

    public NotificationResponse findNotificationById(Long id) {
        var notification = repository.findById(id).orElseThrow(() -> new NotificationNotFound("Notification not found"));

        return mapper.toResponse(notification);
    }

    public NotificationResponse markAsRead(Long id) {
        var notification = repository.findById(id).orElseThrow(() -> new NotificationNotFound("Notification not found"));

        notification.setRead(true);
        var updatedNotification = repository.save(notification);

        return mapper.toResponse(updatedNotification);
    }
}
