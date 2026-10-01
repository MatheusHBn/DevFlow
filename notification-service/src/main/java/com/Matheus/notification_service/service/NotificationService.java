package com.Matheus.notification_service.service;

import com.Matheus.notification_service.domain.Notification;
import com.Matheus.notification_service.messaging.event.TaskCreatedEvent;
import com.Matheus.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;

    public void createNotification(TaskCreatedEvent event) {

        var notification = Notification.builder()
                .taskId(event.taskId())
                .message("Task \"%s\" was created.".formatted(event.title()))
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }
}
