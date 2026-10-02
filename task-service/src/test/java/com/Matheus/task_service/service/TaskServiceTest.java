package com.Matheus.task_service.service;

import com.Matheus.task_service.commons.TaskUtils;
import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.domain.Task;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.mapper.TaskMapper;
import com.Matheus.task_service.messaging.event.TaskCreatedEvent;
import com.Matheus.task_service.messaging.event.TaskStatusChangedEvent;
import com.Matheus.task_service.messaging.producer.TaskEventProducer;
import com.Matheus.task_service.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Spy
    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @InjectMocks
    private TaskService service;

    @Captor
    private ArgumentCaptor<TaskCreatedEvent> eventCaptor;

    @Mock
    private TaskEventProducer eventProducer;
    private TaskUtils taskUtils;

    @BeforeEach
    void setUp() {
        taskUtils = new TaskUtils();
    }

    @Test
    @Order(1)
    @DisplayName("Should create a new task successfully")
    void shouldCreateTaskSuccessfully() {
        var request = taskUtils.createTaskRequest();
        var savedTask = taskUtils.createSavedTask();

        when(repository.save(any(Task.class))).thenReturn(savedTask);

        var result = service.createTask(request);

        assertNotNull(result);
        assertEquals(savedTask.getId(), result.id());

        verify(repository).save(any(Task.class));
        verify(eventProducer).publishTaskCreated(eventCaptor.capture());

        TaskCreatedEvent capturedEvent = eventCaptor.getValue();

        assertEquals(savedTask.getId(), capturedEvent.taskId());
        assertEquals(savedTask.getTitle(), capturedEvent.title());
        assertEquals(savedTask.getStatus(), capturedEvent.status());
        assertEquals(savedTask.getPriority(), capturedEvent.priority());
        assertEquals(savedTask.getDescription(), capturedEvent.description());
    }


    @Test
    @Order(2)
    @DisplayName("Should return a list of all tasks when successful")
    void findAllTasks_ReturnsListOfAllTasks_WhenSuccessful() {
        var task1 = Task.builder().id(1L).title("Task 1").build();
        var task2 = Task.builder().id(2L).title("Task 2").build();

        when(repository.findAll()).thenReturn(List.of(task1, task2));

        var result = service.findAllTasks();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Task 1", result.get(0).title());
        assertEquals(2L, result.get(1).id());
        assertEquals("Task 2", result.get(1).title());

        verify(repository).findAll();
    }


    @Test
    @Order(3)
    @DisplayName("Should return an empty list when there are no tasks")
    void findAllTasks_ReturnsEmptyList_WhenNoTasksExist() {
        when(repository.findAll()).thenReturn(List.of());

        var result = service.findAllTasks();

        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }


    @Test
    @Order(4)
    @DisplayName("Should return task by ID when task exists")
    void findTaskById_ReturnsTask_WhenTaskExists() {
        var task = Task.builder().id(1L).title("Study Java").status(StatusTask.TODO).build();

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        var result = service.findTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Study Java", result.title());

        verify(repository).findById(1L);
    }


    @Test
    @Order(5)
    @DisplayName("Should throw IllegalArgumentException when searching for a non-existing task ID")
    void findTaskById_ThrowsIllegalArgumentException_WhenTaskIdDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.findTaskById(999L));

        verify(repository).findById(999L);
    }


    @Test
    @Order(6)
    @DisplayName("Should update task details when successful")
    void updateTask_UpdatesTaskDetails_WhenSuccessful() {
        var taskInDatabase = Task.builder().id(1L).title("Old Title").status(StatusTask.TODO).build();
        var request = TaskRequest.builder().title("Updated Title").status(StatusTask.IN_PROGRESS).build();

        when(repository.findById(1L)).thenReturn(Optional.of(taskInDatabase));
        when(repository.save(any(Task.class))).then(returnsFirstArg());

        var result = service.updateTask(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Updated Title", result.title());
        assertEquals(StatusTask.IN_PROGRESS, result.status());

        verify(repository).findById(1L);
        verify(repository).save(taskInDatabase);
    }


    @Test
    @Order(7)
    @DisplayName("Should throw IllegalArgumentException when updating a task that does not exist")
    void updateTask_ThrowsIllegalArgumentException_WhenTaskDoesNotExist() {
        TaskRequest request = TaskRequest.builder().title("Task").build();

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.updateTask(999L, request));

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }


    @Test
    @Order(8)
    @DisplayName("Should update task status successfully")
    void updateTaskStatus_UpdatesStatus_WhenSuccessful() {
        var task = Task.builder().id(1L).status(StatusTask.TODO).build();

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).then(returnsFirstArg());

        var result = service.updateTaskStatus(1L, StatusTask.DONE);

        assertEquals(StatusTask.DONE, result.status());
        assertEquals(StatusTask.DONE, task.getStatus());

        verify(repository).findById(1L);
        verify(repository).save(task);
    }


    @Test
    @Order(9)
    @DisplayName("Should throw IllegalArgumentException when updating status of a non-existing task")
    void updateTaskStatus_ThrowsIllegalArgumentException_WhenTaskDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.updateTaskStatus(999L, StatusTask.DONE));

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }


    @Test
    @Order(10)
    @DisplayName("Should delete task successfully when task exists")
    void deleteTask_DeletesTask_WhenSuccessful() {
        Task task = Task.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        service.deleteTask(1L);

        verify(repository).findById(1L);
        verify(repository).delete(task);
    }


    @Test
    @Order(11)
    @DisplayName("Should throw IllegalArgumentException when deleting a non-existing task")
    void deleteTask_ThrowsIllegalArgumentException_WhenTaskDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteTask(999L));

        verify(repository).findById(999L);
        verify(repository, never()).delete(any());
    }

    @Test
    @Order(12)
    @DisplayName("Should publish event when task status is changed")
    void updateTaskStatus_PublishesEvent_WhenStatusIsChanged() {

        var task = Task.builder()
                .id(1L)
                .title("Study Kafka")
                .description("Learn Kafka")
                .status(StatusTask.TODO)
                .priority(PriorityTask.HIGH)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        service.updateTaskStatus(1L, StatusTask.DONE);

        var eventCaptor = ArgumentCaptor.forClass(TaskStatusChangedEvent.class);

        verify(eventProducer).publishTaskStatusChanged(eventCaptor.capture());

        var event = eventCaptor.getValue();

        assertEquals(1L, event.taskId());
        assertEquals("TODO", event.previousStatus());
        assertEquals("DONE", event.newStatus());
    }
}