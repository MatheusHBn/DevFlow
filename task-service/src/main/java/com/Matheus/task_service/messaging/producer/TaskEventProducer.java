package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.messaging.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventProducer {

    private static final String TASK_CREATED_TOPIC = "task-created";
    private static final String TASK_PRIORITY_CHANGED_TOPIC = "task-priority-changed";
    private static final String TASK_STATUS_CHANGED_TOPIC = "task-status-changed";
    private static final String TASK_UPDATED_TOPIC = "task-updated";
    private static final String TASK_DELETED_TOPIC = "task-deleted";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        kafkaTemplate.send(TASK_CREATED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskStatusChanged(TaskStatusChangedEvent event) {
        kafkaTemplate.send(TASK_STATUS_CHANGED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskPriorityChanged(TaskPriorityChangedEvent event){
        kafkaTemplate.send(TASK_PRIORITY_CHANGED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskUpdated(TaskUpdatedEvent event){
        kafkaTemplate.send(TASK_UPDATED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskDeleted(TaskDeletedEvent event){
        kafkaTemplate.send(TASK_DELETED_TOPIC, event.taskId().toString(), event);
    }
}
