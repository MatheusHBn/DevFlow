package com.Matheus.notification_service;

import com.Matheus.notification_service.config.PostgresContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresContainerConfig.class)
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
