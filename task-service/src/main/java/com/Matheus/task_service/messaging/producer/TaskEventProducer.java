package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.messaging.event.TaskCreatedEvent;
import com.Matheus.task_service.messaging.event.TaskStatusChangedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventProducer {

    private static final String TASK_CREATED_TOPIC = "task-created";
    private static final String TASK_STATUS_CHANGED_TOPIC = "task-status-changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        kafkaTemplate.send(TASK_CREATED_TOPIC, event);
    }

    public void publishTaskStatusChanged(TaskStatusChangedEvent event) {
        kafkaTemplate.send(TASK_STATUS_CHANGED_TOPIC, event);
    }
}
