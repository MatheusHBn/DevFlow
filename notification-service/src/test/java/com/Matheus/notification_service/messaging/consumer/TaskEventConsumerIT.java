package com.Matheus.notification_service.messaging.consumer;

import com.Matheus.notification_service.config.PostgresContainerConfig;
import com.Matheus.notification_service.messaging.event.TaskCreatedEvent;
import com.Matheus.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(PostgresContainerConfig.class)
@EmbeddedKafka(partitions = 1, topics = "task-created")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class TaskEventConsumerIT {

    @Autowired
    private KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    @Autowired
    private NotificationRepository repository;

    @Test
    @Order(1)
    @DisplayName("Should receive event from Kafka and save notification in database successfully")
    void consume_CreatesNotificationInDatabase_WhenTaskCreatedEventIsPublished() {
        var event = new TaskCreatedEvent(
                1L,
                "Study Kafka",
                "Learn producers and consumers",
                "TODO",
                "MEDIUM"
        );

        kafkaTemplate.send("task-created", event);

        await()
                .pollInterval(Duration.ofMillis(500))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    var notifications = repository.findAll();
                    var savedNotification = notifications.stream()
                            .filter(notification -> notification.getTaskId().equals(1L))
                            .findFirst()
                            .orElseThrow();

                    assertEquals(1L, savedNotification.getTaskId());
                    assertEquals("Task \"Study Kafka\" was created.", savedNotification.getMessage());
                    assertFalse(savedNotification.isRead());
                    assertNotNull(savedNotification.getCreatedAt());
                });
    }
}
