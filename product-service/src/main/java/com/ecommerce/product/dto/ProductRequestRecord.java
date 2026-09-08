package com.ecommerce.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequestRecord(
    @NotBlank(message = "Product name is mandatory")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    String name,

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    @PositiveOrZero(message = "Price must be positive or zero")
    BigDecimal price,

    @NotNull(message = "SKU is mandatory")
    Integer sku
) {}
