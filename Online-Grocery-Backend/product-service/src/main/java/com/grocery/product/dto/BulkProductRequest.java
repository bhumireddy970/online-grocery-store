package com.grocery.product.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record BulkProductRequest(

        @NotEmpty(message = "Product ids are required")
        List<UUID> productIds

) {
}