package com.Matheus.task_service.api;

import com.Matheus.task_service.commons.FileUtils;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.dto.TaskResponse;
import com.Matheus.task_service.service.TaskService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(FileUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService service;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @Order(1)
    @DisplayName("Should return 201 Created and response body when task is created successfully")
    void createTask_returnsCreated_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("task/task-request-200.json");
        var responseObj = fileUtils.readResourceAsObject("task/task-response-200.json", TaskResponse.class);

        when(service.createTask(any(TaskRequest.class))).thenReturn(responseObj);

        mockMvc.perform(post("/v1/tasks").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Study Kafka"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }


    @Test
    @Order(2)
    @DisplayName("Should return 200 OK and list of tasks when request is successful")
    void findAllTasks_returnsTasks_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceAsObject("task/task-response-200.json", TaskResponse.class);

        when(service.findAllTasks()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/tasks")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Study Kafka"));

        verify(service).findAllTasks();
    }


    @Test
    @Order(4)
    @DisplayName("Should return 200 OK and task details when ID exists")
    void findTaskById_returnsTask_WhenIdExists() throws Exception {
        var response = fileUtils.readResourceAsObject("task/task-response-200.json", TaskResponse.class);

        when(service.findTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/tasks/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Study Kafka"));

        verify(service).findTaskById(1L);
    }


    @Test
    @Order(5)
    @DisplayName("Should return 200 OK and updated task when update is successful")
    void updateTask_returnsUpdatedTask_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("task/task-request-200.json");
        var response = fileUtils.readResourceAsObject("task/task-response-updated-200.json", TaskResponse.class);

        when(service.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/tasks/1").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated task"));

        verify(service).updateTask(eq(1L), any(TaskRequest.class));
    }


    @Test
    @Order(7)
    @DisplayName("Should return 200 OK and updated task status when successful")
    void updateTaskStatus_returnsUpdatedTask_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceAsObject("task/task-response-updated-200.json", TaskResponse.class);

        when(service.updateTaskStatus(1L, StatusTask.IN_PROGRESS)).thenReturn(response);

        mockMvc.perform(patch("/v1/tasks/1/status").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(service).updateTaskStatus(1L, StatusTask.IN_PROGRESS);
    }


    @Test
    @Order(8)
    @DisplayName("Should return 400 Bad Request when task status is invalid")
    void updateTaskStatus_returnsBadRequest_WhenStatusIsInvalid() throws Exception {
        mockMvc.perform(patch("/v1/tasks/1/status").param("status", "INVALID"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }


    @Test
    @Order(9)
    @DisplayName("Should return 204 No Content when task deletion is successful")
    void deleteTask_returnsNoContent_WhenSuccessful() throws Exception {
        doNothing().when(service).deleteTask(1L);

        mockMvc.perform(delete("/v1/tasks/1")).andExpect(status().isNoContent());

        verify(service).deleteTask(1L);
    }
}