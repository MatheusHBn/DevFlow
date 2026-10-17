package com.Matheus.api_gateway.security;

import com.Matheus.api_gateway.service.JwtService;
import com.Matheus.api_gateway.service.RouteAuthorizationService;
import org.junit.jupiter.api.*;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private RouteAuthorizationService routeAuthorizationService;
    private JwtAuthenticationFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        routeAuthorizationService = mock(RouteAuthorizationService.class);

        filter = new JwtAuthenticationFilter(jwtService, routeAuthorizationService);

        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @Order(1)
    @DisplayName("Should allow access to public routes without requiring a token")
    void filter_AllowsRequest_WhenRouteIsPublic() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/v1/auth/login").build());

        when(routeAuthorizationService.isPublicRoute("/v1/auth/login", HttpMethod.POST)).thenReturn(true);

        filter.filter(exchange, chain).block();

        assertNull(exchange.getResponse().getStatusCode());
        verify(chain).filter(exchange);
        verifyNoInteractions(jwtService);
    }

    @Test
    @Order(2)
    @DisplayName("Should return 401 Unauthorized when Authorization header is missing")
    void filter_ReturnsUnauthorized_WhenAuthorizationHeaderIsMissing() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/v1/tasks").build());

        when(routeAuthorizationService.isPublicRoute("/v1/tasks", HttpMethod.GET)).thenReturn(false);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());

        verifyNoInteractions(jwtService);
        verify(chain, never()).filter(any());
    }

    @Test
    @Order(3)
    @DisplayName("Should return 401 Unauthorized when Authorization header lacks Bearer prefix")
    void filter_ReturnsUnauthorized_WhenBearerPrefixIsMissing() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/v1/tasks")
                        .header("Authorization", "token-invalido")
                        .build());

        when(routeAuthorizationService.isPublicRoute("/v1/tasks", HttpMethod.GET)).thenReturn(false);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());

        verifyNoInteractions(jwtService);
        verify(chain, never()).filter(any());
    }

    @Test
    @Order(4)
    @DisplayName("Should return 401 Unauthorized when the provided token is invalid")
    void filter_ReturnsUnauthorized_WhenTokenIsInvalid() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/v1/tasks")
                        .header("Authorization", "Bearer token-invalido")
                        .build());

        when(routeAuthorizationService.isPublicRoute("/v1/tasks", HttpMethod.GET)).thenReturn(false);
        when(jwtService.isTokenValid("token-invalido")).thenReturn(false);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());

        verify(chain, never()).filter(any());
    }

    @Test
    @Order(5)
    @DisplayName("Should return 403 Forbidden when the valid token does not contain a role claim")
    void filter_ReturnsForbidden_WhenTokenHasNoRoleClaim() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/v1/tasks")
                        .header("Authorization", "Bearer token-valido")
                        .build());

        when(routeAuthorizationService.isPublicRoute("/v1/tasks", HttpMethod.GET)).thenReturn(false);
        when(jwtService.isTokenValid("token-valido")).thenReturn(true);
        when(jwtService.extractRole("token-valido")).thenReturn(null);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());

        verify(chain, never()).filter(any());
    }

    @Test
    @Order(6)
    @DisplayName("Should return 403 Forbidden when the user role is not authorized for the route")
    void filter_ReturnsForbidden_WhenRoleIsInsufficientForRoute() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/v1/audits")
                        .header("Authorization", "Bearer token-valido")
                        .build());

        when(routeAuthorizationService.isPublicRoute("/v1/audits", HttpMethod.GET)).thenReturn(false);
        when(jwtService.isTokenValid("token-valido")).thenReturn(true);
        when(jwtService.extractRole("token-valido")).thenReturn("ROLE_USER");
        when(routeAuthorizationService.isAuthorized("/v1/audits", "ROLE_USER")).thenReturn(false);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());

        verify(chain, never()).filter(any());
    }

    @Test
    @Order(7)
    @DisplayName("Should proceed with filter chain when token is valid and role is authorized")
    void filter_ProceedsWithChain_WhenTokenAndRoleAreValid() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/v1/tasks")
                        .header("Authorization", "Bearer token-valido")
                        .build());

        when(routeAuthorizationService.isPublicRoute("/v1/tasks", HttpMethod.GET)).thenReturn(false);
        when(jwtService.isTokenValid("token-valido")).thenReturn(true);
        when(jwtService.extractRole("token-valido")).thenReturn("ROLE_USER");
        when(routeAuthorizationService.isAuthorized("/v1/tasks", "ROLE_USER")).thenReturn(true);

        filter.filter(exchange, chain).block();

        assertNull(exchange.getResponse().getStatusCode());
        verify(chain).filter(exchange);
    }
}