package com.Matheus.task_service.messaging.producer;

import com.Matheus.task_service.domain.PriorityTask;
import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.messaging.event.TaskCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = "task-created")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskEventProducerIT {

    @Autowired
    private TaskEventProducer producer;

    @Autowired
    private TestKafkaListener listener;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @BeforeEach
    void setUp() {
        listener.records.clear();

        var container = registry.getListenerContainer("producer-test");

        assertNotNull(container);

        ContainerTestUtils.waitForAssignment(container, 3);
    }

    @Test
    @Order(1)
    @DisplayName("Should use task id as key when publishing task created event")
    void publishTaskCreated_UsesTaskIdAsKey_WhenSuccessful() throws Exception {
        var event = new TaskCreatedEvent(
                UUID.randomUUID(),
                42L,
                "Task 42",
                "Description",
                StatusTask.TODO,
                PriorityTask.MEDIUM);

        producer.publishTaskCreated(event);

        var record = listener.records.poll(10, TimeUnit.SECONDS);

        assertNotNull(record);
        assertEquals("42", record.key());
    }

    @Test
    @Order(2)
    @DisplayName("Should route events with the same key to the same partition")
    void publishTaskCreated_RoutesSameKeyToSamePartition_WhenMultipleEventsPublished() throws Exception {
        var event1 = new TaskCreatedEvent(
                UUID.randomUUID(),
                42L,
                "Task 42 - 1",
                "Description",
                StatusTask.TODO,
                PriorityTask.MEDIUM);

        var event2 = new TaskCreatedEvent(
                UUID.randomUUID(),
                42L,
                "Task 42 - 2",
                "Description",
                StatusTask.TODO,
                PriorityTask.HIGH);

        producer.publishTaskCreated(event1);
        producer.publishTaskCreated(event2);

        var record1 = listener.records.poll(10, TimeUnit.SECONDS);
        var record2 = listener.records.poll(10, TimeUnit.SECONDS);

        assertNotNull(record1);
        assertNotNull(record2);
        assertEquals("42", record1.key());
        assertEquals("42", record2.key());
        assertEquals(record1.partition(), record2.partition());
    }

    @TestConfiguration
    static class TestKafkaListener {
        final BlockingQueue<ConsumerRecord<String, TaskCreatedEvent>> records = new LinkedBlockingQueue<>();

        @KafkaListener(id = "producer-test", topics = "task-created", groupId = "producer-test")
        void listen(ConsumerRecord<String, TaskCreatedEvent> record) {
            records.add(record);
        }
    }
}
