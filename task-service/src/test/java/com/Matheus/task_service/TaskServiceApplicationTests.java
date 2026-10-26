package com.Matheus.task_service;

import com.Matheus.task_service.config.PostgresContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresContainerConfig.class)
class TaskServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
