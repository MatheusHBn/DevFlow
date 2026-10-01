package com.Matheus.notification_service.repository;

import com.Matheus.notification_service.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
