package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponseRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import com.ecommerce.order.exception.ProductServiceUnavailableException;
import java.util.UUID;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient productRestClient) {
        this.restClient = productRestClient;
    }

    @Bulkhead(
        name = "productService",
        type = Bulkhead.Type.SEMAPHORE
    )
    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "productServiceFallback"
    )
    @Retryable(
        retryFor = Exception.class,
        maxAttempts = 3,
        backoff = @Backoff(
                delay = 200,
                multiplier = 2,
                random = true
        )
    )
    
    public ProductResponseRecord getProduct(UUID productId) {

        return restClient
                .get()
                .uri("/api/v1/products/{id}", productId)
                .retrieve()
                .body(ProductResponseRecord.class);
    }

    public ProductResponseRecord productServiceFallback(
            UUID productId,
            Throwable throwable) {

        throw new ProductServiceUnavailableException(
                "Product Service is currently unavailable",
                throwable
        );
    }
}