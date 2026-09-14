package com.ecommerce.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GatewayRequestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String requestId = UUID.randomUUID().toString();

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-Gateway-Request-ID", requestId)
                .build();

        ServerWebExchange modifiedExchange = exchange
                .mutate()
                .request(request)
                .build();

        System.out.println(
                "Gateway Request ID: " + requestId +
                " | Path: " + request.getURI().getPath()
        );

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}