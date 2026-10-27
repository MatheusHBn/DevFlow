package com.Matheus.audit_service;

import com.Matheus.audit_service.config.PostgresContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresContainerConfig.class)
class AuditServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
