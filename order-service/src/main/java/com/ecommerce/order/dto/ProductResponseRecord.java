package com.ecommerce.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponseRecord(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Integer sku,
    Instant createdAt
) {}
