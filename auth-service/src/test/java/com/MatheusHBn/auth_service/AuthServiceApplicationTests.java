package com.MatheusHBn.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"jwt.secret=VGhpc0lzQVNlY3JldEtleUZvclRlc3RzMTIzNDU2Nzg5MA==",
		"jwt.expiration=3600000"
})
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
