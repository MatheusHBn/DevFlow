package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.messaging.event.TaskCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventProducer {

    private static final String TASK_CREATED_TOPIC = "task-created";

    private final KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        kafkaTemplate.send(TASK_CREATED_TOPIC, event);
    }
}
