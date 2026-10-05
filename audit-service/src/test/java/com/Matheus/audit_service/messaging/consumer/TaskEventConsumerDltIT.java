package com.Matheus.audit_service.messaging.consumer;

import com.Matheus.audit_service.config.PostgresContainerConfig;
import com.Matheus.audit_service.messaging.event.TaskCreatedEvent;
import com.Matheus.audit_service.service.AuditService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(PostgresContainerConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"task-created", "task-created.DLT"})
@ActiveProfiles("test")
public class TaskEventConsumerDltIT {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private AuditService service;

    @Autowired
    private DltTestListener listener;

    @BeforeEach
    void setUp() {
        listener.messages.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Deve tentar 4 vezes (1 normal + 3 retries) e enviar para DLT quando sempre falhar")
    void shouldRetryAndSendToDltWhenProcessingAlwaysFails() throws InterruptedException {
        var event = new TaskCreatedEvent(1L, "Fail Task", "Desc", "TODO", "HIGH");

        doThrow(new RuntimeException("Simulated Database Error")).when(service).createAuditLog(any(TaskCreatedEvent.class));

        kafkaTemplate.send("task-created", event);

        verify(service, timeout(10000).times(4)).createAuditLog(any(TaskCreatedEvent.class));

        var dltMessage = listener.messages.poll(10, TimeUnit.SECONDS);

        assertThat(dltMessage).isNotNull().extracting(TaskCreatedEvent::title).isEqualTo("Fail Task");
    }

    @Test
    @Order(2)
    @DisplayName("Deve processar de primeira e não fazer retry quando houver sucesso")
    void shouldNotRetryWhenProcessingSucceeds() {
        var event = new TaskCreatedEvent(2L, "Success Task", "Desc", "TODO", "HIGH");

        doNothing().when(service).createAuditLog(any(TaskCreatedEvent.class));

        kafkaTemplate.send("task-created", event);

        verify(service, timeout(5000).times(1)).createAuditLog(any(TaskCreatedEvent.class));
    }

    @Test
    @Order(3)
    @DisplayName("Deve parar de tentar assim que processar com sucesso (ex: falha 2x e acerta na 3ª)")
    void shouldStopRetryingWhenProcessingSucceeds() {
        var event = new TaskCreatedEvent(3L, "Recovery Task", "Desc", "TODO", "HIGH");

        doThrow(new RuntimeException("Error 1")).doThrow(new RuntimeException("Error 2")).doNothing()
                .when(service).createAuditLog(any(TaskCreatedEvent.class));

        kafkaTemplate.send("task-created", event);

        verify(service, timeout(10000).times(3)).createAuditLog(any(TaskCreatedEvent.class));
    }

    @TestConfiguration
    static class DltTestListener {
        final BlockingQueue<TaskCreatedEvent> messages = new LinkedBlockingQueue<>();

        @KafkaListener(topics = "task-created.DLT", groupId = "dlt-test-group")
        void listen(TaskCreatedEvent message) {
            messages.add(message);
        }
    }
}
