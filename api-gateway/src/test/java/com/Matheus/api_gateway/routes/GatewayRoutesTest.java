package com.Matheus.api_gateway.routes;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "JWT_SECRET=ZGV2Zmxvdy10ZXN0LXNlY3JldC1rZXktMzItYnl0ZXM="
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GatewayRoutesTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    @Order(1)
    @DisplayName("Should register all configured microservice routes on startup")
    void getRoutes_ReturnsAllConfiguredRoutes_WhenApplicationStarts() {
        List<Route> routes = Flux.from(routeLocator.getRoutes()).collectList().block();

        assertThat(routes).extracting(Route::getId).containsExactlyInAnyOrder(
                "task-service",
                "notification-service",
                "audit-service",
                "auth-service");
    }
}
