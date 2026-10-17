package com.Matheus.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"JWT_SECRET=ZGV2Zmxvdy10ZXN0LXNlY3JldC1rZXktMzItYnl0ZXM="
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
