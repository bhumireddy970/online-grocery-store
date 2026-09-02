package com.grocery.inventory.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID productId,
        Integer availableQuantity,
        Integer reservedQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
