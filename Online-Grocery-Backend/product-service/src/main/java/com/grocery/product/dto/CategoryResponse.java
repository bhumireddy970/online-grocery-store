package com.grocery.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        @Schema(description = "Category created successfully",example = "123")
        UUID id,
        @Schema(description = "Category created successfully",example = "Vegetables")
        String name,
        @Schema(description = "Category created successfully",example = "Fresh seasonal Vegetables")
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
