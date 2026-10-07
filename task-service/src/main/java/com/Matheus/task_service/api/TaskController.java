package com.Matheus.task_service.api;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.dto.TaskResponse;
import com.Matheus.task_service.dto.TaskUpdateRequest;
import com.Matheus.task_service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Endpoints for managing tasks")
public class TaskController {

    private final TaskService service;

    @Operation(summary = "Create a new task", description = "Creates a new task and publishes a TaskCreated event to Kafka.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task successfully created",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTask(request));
    }

    @Operation(summary = "Get all tasks", description = "Returns all tasks stored in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks successfully retrieved",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAllTasks() {
        return ResponseEntity.ok(service.findAllTasks());
    }

    @Operation(summary = "Get a task by ID", description = "Returns a specific task using its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully retrieved",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findTaskById(
            @Parameter(description = "Unique identifier of the task", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findTaskById(id));
    }

    @Operation(summary = "Update a task", description = "Updates the editable information of an existing task.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Unique identifier of the task", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {

        return ResponseEntity.ok(service.updateTask(id, request));
    }

    @Operation(summary = "Update task status",
            description = "Changes the status of an existing task and publishes a TaskStatusChanged event.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status successfully updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @Parameter(description = "Unique identifier of the task", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New status for the task", example = "IN_PROGRESS")
            @RequestParam StatusTask status) {

        return ResponseEntity.ok(service.updateTaskStatus(id, status));
    }

    @Operation(summary = "Update task priority",
            description = "Changes the priority of an existing task and publishes a TaskPriorityChanged event.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task priority successfully updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskResponse> updateTaskPriority(
            @Parameter(description = "Unique identifier of the task", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New priority for the task", example = "HIGH")
            @RequestParam PriorityTask priority) {

        return ResponseEntity.ok(service.updateTaskPriority(id, priority));
    }

    @Operation(summary = "Delete a task", description = "Deletes an existing task and publishes a TaskDeleted event.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Unique identifier of the task", example = "1")
            @PathVariable Long id) {

        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
