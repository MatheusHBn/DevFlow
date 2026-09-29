package com.Matheus.task_service.commons;


import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.domain.Task;
import com.Matheus.task_service.dto.TaskRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class TaskUtils {

    public Task createTask(){
        return Task.builder().title("Study Kafka").description("Description test").status(StatusTask.TODO).priority(PriorityTask.MEDIUM)
                .dueDate(LocalDateTime.now().plusDays(60)).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    public Task createSavedTask(){
        return Task.builder().id(1L).title("Study Kafka").description("Description test").status(StatusTask.TODO).priority(PriorityTask.MEDIUM)
                .dueDate(LocalDateTime.now().plusDays(60)).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now().plusDays(3)).build();
    }

    public TaskRequest createTaskRequest(){
        return TaskRequest.builder().title("Study Kafka").description("Learn producers and consumers").status(StatusTask.TODO).priority(PriorityTask.MEDIUM).projectId(null).build();
    }

}
