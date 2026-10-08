package com.Matheus.notification_service.api;

import com.Matheus.notification_service.commons.FileUtils;
import com.Matheus.notification_service.dto.NotificationResponse;
import com.Matheus.notification_service.service.NotificationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(FileUtils.class)
class NotificationControllerTest {

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService service;

    @Test
    @Order(1)
    @DisplayName("Should return a list of all notifications when successful")
    void findAll_ReturnsAllNotifications_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceAsObject("notification/notification-response-200.json", NotificationResponse.class);

        when(service.findAllNotifications()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/notifications").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskId").value(42))
                .andExpect(jsonPath("$[0].message")
                        .value("Task \"Implement JWT authentication\" was created."))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(service).findAllNotifications();
    }

    @Test
    @Order(2)
    @DisplayName("Should return notification by ID when it exists")
    void findById_ReturnsNotification_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceAsObject("notification/notification-response-200.json", NotificationResponse.class);

        when(service.findNotificationById(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/notifications/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskId").value(42))
                .andExpect(jsonPath("$.message")
                        .value("Task \"Implement JWT authentication\" was created."))
                .andExpect(jsonPath("$.read").value(false));

        verify(service).findNotificationById(1L);
    }

    @Test
    @Order(3)
    @DisplayName("Should return an empty list when no notifications exist")
    void findAll_ReturnsEmptyList_WhenNoNotificationsExist() throws Exception {

        when(service.findAllNotifications()).thenReturn(List.of());

        mockMvc.perform(get("/v1/notifications").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}