package com.Matheus.task_service.api;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.dto.TaskResponse;
import com.Matheus.task_service.dto.TaskUpdateRequest;
import com.Matheus.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTask(request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAllTasks() {
        return ResponseEntity.ok(service.findAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(service.updateTask(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id, @RequestParam StatusTask status) {
        return ResponseEntity.ok(service.updateTaskStatus(id, status));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskResponse> updateTaskPriority(@PathVariable Long id, @RequestParam PriorityTask priority) {
        return ResponseEntity.ok(service.updateTaskPriority(id, priority));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
