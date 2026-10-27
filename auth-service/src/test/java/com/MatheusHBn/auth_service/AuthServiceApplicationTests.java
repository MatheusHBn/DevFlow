package com.MatheusHBn.auth_service;

import com.MatheusHBn.auth_service.config.PostgresContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
		"jwt.secret=VGhpc0lzQVNlY3JldEtleUZvclRlc3RzMTIzNDU2Nzg5MA==",
		"jwt.expiration=3600000"
})
@Import(PostgresContainerConfig.class)
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
