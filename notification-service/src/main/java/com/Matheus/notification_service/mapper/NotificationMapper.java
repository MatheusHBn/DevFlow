package com.Matheus.notification_service.mapper;

import com.Matheus.notification_service.domain.Notification;
import com.Matheus.notification_service.dto.NotificationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
