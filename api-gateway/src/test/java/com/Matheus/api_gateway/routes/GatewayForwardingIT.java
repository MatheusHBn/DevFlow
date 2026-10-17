package com.Matheus.api_gateway.routes;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "JWT_SECRET=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                "TASK_SERVICE_URL=http://localhost:18081",
                "AUDIT_SERVICE_URL=http://localhost:18083"
        })
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GatewayForwardingIT {

    private static WireMockServer taskService;
    private static WireMockServer auditService;

    @LocalServerPort
    private int gatewayPort;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void startMockService() {
        taskService = new WireMockServer(options().port(18081));
        taskService.start();

        taskService.stubFor(get(urlPathMatching("/v1/tasks.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"task-service reached\"}")));

        auditService = new WireMockServer(options().port(18083));
        auditService.start();

        auditService.stubFor(get(urlPathMatching("/v1/audits.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"audit-service reached\"}")));
    }

    @AfterAll
    static void stopMockService() {
        if (taskService != null) {
            taskService.stop();
        }

        if (auditService != null) {
            auditService.stop();
        }
    }

    private String generateUserToken() {
        byte[] keyBytes = Base64.getDecoder().decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");

        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject("matheus")
                .claim("role", "ROLE_USER")
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3_600_000))
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    private String generateAdminToken() {
        byte[] keyBytes = Base64.getDecoder().decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");

        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject("admin")
                .claim("role", "ROLE_ADMIN")
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3_600_000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Test
    @Order(1)
    @DisplayName("Should forward request to task-service when user token is valid and authorized")
    void forwardRequest_ReturnsOk_WhenUserIsAuthorized() {
        String token = generateUserToken();

        webTestClient.get()
                .uri("/v1/tasks")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("task-service reached");

        taskService.verify(getRequestedFor(urlPathEqualTo("/v1/tasks")));
    }

    @Test
    @Order(2)
    @DisplayName("Should return 403 Forbidden when user with ROLE_USER attempts to access audits")
    void forwardRequest_ReturnsForbidden_WhenUserRoleIsInsufficient() {
        String token = generateUserToken();

        webTestClient.get()
                .uri("/v1/audits")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden();

    }

    @Test
    @Order(3)
    @DisplayName("Should allow request to audit-service when user has ROLE_ADMIN")
    void forwardRequest_ReturnsOk_WhenUserHasAdminRole() {
        String token = generateAdminToken();

        webTestClient.get()
                .uri("/v1/audits")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        auditService.verify(getRequestedFor(urlPathEqualTo("/v1/audits")));
    }
}
