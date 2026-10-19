package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.messaging.event.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private static final Logger log = LoggerFactory.getLogger(TaskEventProducer.class);

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        log.info("Publishing task-created event for taskId={}", event.taskId());

        sendEvent(TASK_CREATED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskStatusChanged(TaskStatusChangedEvent event) {
        log.info("Publishing task-status-changed event for taskId={}", event.taskId());

        sendEvent(TASK_STATUS_CHANGED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskPriorityChanged(TaskPriorityChangedEvent event) {
        log.info("Publishing task-priority-changed event for taskId={}", event.taskId());

        sendEvent(TASK_PRIORITY_CHANGED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskUpdated(TaskUpdatedEvent event) {
        log.info("Publishing task-updated event for taskId={}", event.taskId());

        sendEvent(TASK_UPDATED_TOPIC, event.taskId().toString(), event);
    }

    public void publishTaskDeleted(TaskDeletedEvent event) {
        log.info("Publishing task-deleted event for taskId={}", event.taskId());

        sendEvent(TASK_DELETED_TOPIC, event.taskId().toString(), event);
    }

    private void sendEvent(String topic, String key, Object event) {
        String correlationId = MDC.get("correlationId");

        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, event);

        if (correlationId != null) {
            record.headers().add("X-Correlation-ID", correlationId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        kafkaTemplate.send(record);
    }
}
