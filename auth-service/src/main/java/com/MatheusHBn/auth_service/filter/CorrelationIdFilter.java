package com.MatheusHBn.auth_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter{

    private static final String CORRELATION_ID = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = resolveCorrelationId(request.getHeader(CORRELATION_ID));

        MDC.put("correlationId", correlationId);
        response.setHeader(CORRELATION_ID, correlationId);
        log.info(
                "Incoming request: method={}, path={}, correlationId={}",
                request.getMethod(),
                request.getRequestURI(),
                correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info(
                    "Request completed: method={}, path={}, status={}, correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    correlationId);
            MDC.remove("correlationId");
        }
    }

    private String resolveCorrelationId(String incomingId) {
        if (incomingId != null) {
            try {
                return UUID.fromString(incomingId).toString();
            } catch (IllegalArgumentException ignored) {

            }
        }

        return UUID.randomUUID().toString();
    }
}
