package com.Matheus.api_gateway.security;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "JWT_SECRET=ZGV2Zmxvdy10ZXN0LXNlY3JldC1rZXktMzItYnl0ZXM="
        })
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewaySecurityIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    @DisplayName("Should return 401 Unauthorized when accessing tasks route without a token")
    void accessTasksRoute_ReturnsUnauthorized_WhenTokenIsMissing() {
        webTestClient.get().uri("/v1/tasks").exchange().expectStatus().isUnauthorized();
    }

    @Test
    @Order(2)
    @DisplayName("Should return 401 Unauthorized when accessing audits route without a token")
    void accessAuditsRoute_ReturnsUnauthorized_WhenTokenIsMissing() {
        webTestClient.get().uri("/v1/audits").exchange().expectStatus().isUnauthorized();
    }

    @Test
    @Order(3)
    @DisplayName("Should not return 401 Unauthorized when accessing the public login route without a token")
    void accessLoginRoute_DoesNotReturnUnauthorized_WhenRouteIsPublic() {
        webTestClient.post()
                .uri("/v1/auth/login")
                .exchange()
                .expectStatus()
                .value(status -> {
                    if (status == HttpStatus.UNAUTHORIZED.value()) {
                        throw new AssertionError("A rota pública de login não deveria exigir JWT");
                    }
                });
    }
}
