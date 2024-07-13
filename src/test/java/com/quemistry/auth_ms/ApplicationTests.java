package com.quemistry.auth_ms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"REDIS_HOST=testValue",
		"REDIS_PORT=123",
		"COGNITO_URL=testValue",
		"QUEMISTRY_CLIENT_WEB_URL=http://localhost:3000"
})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
