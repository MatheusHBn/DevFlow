package com.Matheus.task_service.service;

import com.Matheus.task_service.commons.TaskUtils;
import com.Matheus.task_service.config.PostgresContainerConfig;
import com.Matheus.task_service.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Import(PostgresContainerConfig.class)
class TaskRepositoryIT {

    @Autowired
    private TaskRepository repository;

    private TaskUtils taskUtils;

    @BeforeEach
    void setUp(){
        taskUtils = new TaskUtils();
    }

    @Test
    @Order(1)
    @DisplayName("Should save task and generate ID when successful")
    void save_PersistsTask_WhenSuccessful() {
        var task = taskUtils.createTask();
        var savedTask = repository.save(task);

        assertNotNull(savedTask.getId());
        assertEquals("Study Kafka", savedTask.getTitle());
    }


    @Test
    @Order(2)
    @DisplayName("Should return task by ID when task exists in the database")
    void findById_ReturnsTask_WhenSuccessful() {
        var task = taskUtils.createTask();
        var savedTask = repository.save(task);
        var result = repository.findById(savedTask.getId());

        assertTrue(result.isPresent());
        assertEquals("Study Kafka", result.get().getTitle());
    }


    @Test
    @Order(3)
    @DisplayName("Should return a list of all saved tasks when successful")
    void findAll_ReturnsAllTasks_WhenSuccessful() {
        var task1 = taskUtils.createTask();
        var task2 = taskUtils.createTask();

        repository.saveAll(List.of(task1, task2));

        var result = repository.findAll();

        assertTrue(result.size() >= 2);
    }


    @Test
    @Order(4)
    @DisplayName("Should delete task successfully when task exists")
    void deleteById_RemovesTask_WhenSuccessful() {
        var task = taskUtils.createTask();
        var savedTask = repository.save(task);
        var id = savedTask.getId();

        repository.deleteById(id);

        assertFalse(repository.findById(id).isPresent());
    }
}