package com.Matheus.api_gateway.service;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class RouteAuthorizationService {

    public boolean isPublicRoute(String path, HttpMethod method) {
        return method == HttpMethod.POST && (path.equals("/v1/auth/login") || path.equals("/v1/auth/register"));
    }

    public boolean isAuthorized(String path, String role) {
        if (path.equals("/v1/audits") || path.startsWith("/v1/audits/")) {
            return role.equals("ROLE_ADMIN");
        }

        if (path.equals("/v1/tasks") || path.startsWith("/v1/tasks/")) {
            return role.equals("ROLE_USER") || role.equals("ROLE_ADMIN");
        }

        if (path.equals("/v1/notifications") || path.startsWith("/v1/notifications/")) {
            return role.equals("ROLE_USER") || role.equals("ROLE_ADMIN");
        }

        return false;
    }
}
