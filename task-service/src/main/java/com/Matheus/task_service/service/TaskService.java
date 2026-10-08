package com.Matheus.task_service.service;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.domain.Task;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.dto.TaskResponse;
import com.Matheus.task_service.dto.TaskUpdateRequest;
import com.Matheus.task_service.exception.InvalidTaskException;
import com.Matheus.task_service.exception.TaskNotFound;
import com.Matheus.task_service.mapper.TaskMapper;
import com.Matheus.task_service.messaging.event.*;
import com.Matheus.task_service.messaging.producer.TaskEventProducer;
import com.Matheus.task_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final TaskEventProducer eventProducer;

    public TaskResponse createTask(TaskRequest request){
        var task = mapper.toEntity(request);

        if (task.getStatus() == null) {
            task.setStatus(StatusTask.TODO);
        }

        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        var savedTask = repository.save(task);

        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getStatus(),
                savedTask.getPriority());

        eventProducer.publishTaskCreated(event);

        return mapper.toResponse(savedTask);
    }

    public List<TaskResponse> findAllTasks(){
        var task = repository.findAll();
        return task.stream().map(mapper::toResponse).toList();
    }

    public TaskResponse findTaskById(Long id){
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFound("Id not found"));
        return mapper.toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest request){
        var task = repository.findById(id).orElseThrow(() -> new TaskNotFound("Task not found"));

        mapper.updateEntity(request, task);
        task.setUpdatedAt(LocalDateTime.now());

        var updatedTask = repository.save(task);

        var event = new TaskUpdatedEvent(
                UUID.randomUUID(),
                updatedTask.getId(),
                updatedTask.getTitle());

        eventProducer.publishTaskUpdated(event);

        return mapper.toResponse(updatedTask);
    }

    public TaskResponse updateTaskStatus(Long id, StatusTask status){
        var task = repository.findById(id).orElseThrow(() -> new TaskNotFound("Task not found"));
        var previousStatus = task.getStatus();

        if (previousStatus == StatusTask.DONE && status != StatusTask.DONE) {
            throw new InvalidTaskException("A completed task cannot change its status.");
        }

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());

        var updatedTask = repository.save(task);

        var event = new TaskStatusChangedEvent(
                UUID.randomUUID(),
                updatedTask.getId(),
                previousStatus.name(),
                updatedTask.getStatus().name());

        eventProducer.publishTaskStatusChanged(event);

        return mapper.toResponse(updatedTask);
    }

    public TaskResponse updateTaskPriority(Long id, PriorityTask priority) {
        var task = repository.findById(id).orElseThrow(() -> new TaskNotFound("Task not found"));
        var previousPriority = task.getPriority();

        task.setPriority(priority);
        task.setUpdatedAt(LocalDateTime.now());

        var updatedTask = repository.save(task);
        var event = new TaskPriorityChangedEvent(
                UUID.randomUUID(),
                updatedTask.getId(),
                previousPriority.name(),
                updatedTask.getPriority().name());

        eventProducer.publishTaskPriorityChanged(event);

        return mapper.toResponse(updatedTask);
    }

    public void deleteTask(Long id){
        var task = repository.findById(id).orElseThrow(() -> new TaskNotFound("Task not found"));

        var event = new TaskDeletedEvent(
                UUID.randomUUID(),
                task.getId(),
                task.getTitle());

        repository.delete(task);

        eventProducer.publishTaskDeleted(event);
    }
}
