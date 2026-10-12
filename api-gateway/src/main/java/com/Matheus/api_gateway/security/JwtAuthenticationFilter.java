package com.Matheus.api_gateway.security;

import com.Matheus.api_gateway.service.JwtService;
import com.Matheus.api_gateway.service.RouteAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final RouteAuthorizationService routeAuthorizationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        var method = exchange.getRequest().getMethod();

        if (routeAuthorizationService.isPublicRoute(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return unauthorized(exchange);
        }

        String role = jwtService.extractRole(token);

        if (role == null) {
            return forbidden(exchange);
        }

        if (!routeAuthorizationService.isAuthorized(path, role)) {
            return forbidden(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

        return exchange.getResponse().setComplete();
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
