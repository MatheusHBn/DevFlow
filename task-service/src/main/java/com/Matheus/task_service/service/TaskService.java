package com.Matheus.task_service.service;

import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.domain.Task;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.dto.TaskResponse;
import com.Matheus.task_service.mapper.TaskMapper;
import com.Matheus.task_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskResponse createTask(TaskRequest request){
        var task = mapper.toEntity(request);

        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        var taskSaved = repository.save(task);

        return mapper.toResponse(taskSaved);
    }

    public List<TaskResponse> findAllTasks(){
        var task = repository.findAll();
        return task.stream().map(mapper::toResponse).toList();
    }

    public TaskResponse findTaskById(Long id){
        Task task = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id not found"));
        return mapper.toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request){
        var task = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        mapper.updateEntity(request, task);

        task.setUpdatedAt(LocalDateTime.now());
        var updatedTask = repository.save(task);

        return mapper.toResponse(updatedTask);
    }

    public TaskResponse updateTaskStatus(Long id, StatusTask status){
        var task = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());

        var updatedTask = repository.save(task);

        return mapper.toResponse(updatedTask);
    }

    public void deleteTask(Long id){
        var task = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));

        repository.delete(task);
    }


}
