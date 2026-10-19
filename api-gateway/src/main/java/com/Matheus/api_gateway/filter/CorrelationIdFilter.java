package com.Matheus.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered{

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private static final String CORRELATION_ID = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = UUID.randomUUID().toString();
        long startTime = System.nanoTime();

        ServerHttpRequest request = exchange.getRequest().mutate().headers
                        (headers -> headers.set(CORRELATION_ID, correlationId))
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request)
                .build();

        mutatedExchange.getResponse().getHeaders().set(CORRELATION_ID, correlationId);

        log.info("Incoming request: method={}, path={}, correlationId={}",
                request.getMethod(),
                request.getURI().getPath(),
                correlationId);

        return chain.filter(mutatedExchange).doOnError(
                error -> log.error(
                                "Request failed: method={}, path={}, correlationId={}, error={}",
                                request.getMethod(),
                                request.getURI().getPath(),
                                correlationId,
                                error.toString()))
                .doFinally(signalType -> {
                    HttpStatusCode statusCode = mutatedExchange.getResponse().getStatusCode();

                    long durationMs = (System.nanoTime() - startTime) / 1_000_000;

                    log.info("Request completed: method={}, path={}, status={}, durationMs={}, signal={}, correlationId={}",
                            request.getMethod(),
                            request.getURI().getPath(),
                            statusCode != null ? statusCode.value() : "not_committed",
                            durationMs,
                            signalType,
                            correlationId);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
