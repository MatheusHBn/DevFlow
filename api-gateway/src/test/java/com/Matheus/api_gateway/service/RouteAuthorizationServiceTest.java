package com.Matheus.api_gateway.service;

import org.junit.jupiter.api.*;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RouteAuthorizationServiceTest {

    private final RouteAuthorizationService service = new RouteAuthorizationService();

    @Test
    @Order(1)
    @DisplayName("Should return true for login and register routes when HTTP method is POST")
    void isPublicRoute_ReturnsTrue_WhenRouteIsLoginOrRegisterAndMethodIsPost() {
        assertTrue(service.isPublicRoute("/v1/auth/login", HttpMethod.POST));
        assertTrue(service.isPublicRoute("/v1/auth/register", HttpMethod.POST));
    }

    @Test
    @Order(2)
    @DisplayName("Should return false for public routes when using unsupported HTTP methods")
    void isPublicRoute_ReturnsFalse_WhenMethodIsNotPost() {
        assertFalse(service.isPublicRoute("/v1/auth/login", HttpMethod.GET));
        assertFalse(service.isPublicRoute("/v1/auth/register", HttpMethod.PUT));
    }

    @Test
    @Order(3)
    @DisplayName("Should return false for non-public authentication routes")
    void isPublicRoute_ReturnsFalse_WhenRouteIsNotLoginOrRegister() {
        assertFalse(service.isPublicRoute("/v1/auth/me", HttpMethod.GET));
    }

    @Test
    @Order(4)
    @DisplayName("Should authorize USER and ADMIN roles to access task routes")
    void isAuthorized_ReturnsTrue_WhenAccessingTasksWithValidRole() {
        assertTrue(service.isAuthorized("/v1/tasks", "ROLE_USER"));
        assertTrue(service.isAuthorized("/v1/tasks/123", "ROLE_ADMIN"));
    }

    @Test
    @Order(5)
    @DisplayName("Should authorize USER and ADMIN roles to access notification routes")
    void isAuthorized_ReturnsTrue_WhenAccessingNotificationsWithValidRole() {
        assertTrue(service.isAuthorized("/v1/notifications", "ROLE_USER"));
        assertTrue(service.isAuthorized("/v1/notifications/123", "ROLE_ADMIN"));
    }

    @Test
    @Order(6)
    @DisplayName("Should authorize only ADMIN role to access audit routes and reject USER role")
    void isAuthorized_ReturnsTrueForAdminOnly_WhenAccessingAudits() {
        assertTrue(service.isAuthorized("/v1/audits", "ROLE_ADMIN"));
        assertTrue(service.isAuthorized("/v1/audits/123", "ROLE_ADMIN"));
        assertFalse(service.isAuthorized("/v1/audits", "ROLE_USER"));
    }

    @Test
    @Order(7)
    @DisplayName("Should deny access to unknown routes regardless of role")
    void isAuthorized_ReturnsFalse_WhenRouteIsUnknown() {
        assertFalse(service.isAuthorized("/v1/unknown", "ROLE_ADMIN"));
        assertFalse(service.isAuthorized("/v1/unknown", "ROLE_USER"));
    }

    @Test
    @Order(8)
    @DisplayName("Should deny access to valid routes when role is unknown")
    void isAuthorized_ReturnsFalse_WhenRoleIsUnknown() {
        assertFalse(service.isAuthorized("/v1/tasks", "ROLE_GUEST"));
        assertFalse(service.isAuthorized("/v1/notifications", "ROLE_GUEST"));
    }
}