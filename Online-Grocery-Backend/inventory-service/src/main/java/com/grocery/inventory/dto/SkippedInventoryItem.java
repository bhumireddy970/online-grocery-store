package com.grocery.inventory.dto;

import java.util.UUID;

public record SkippedInventoryItem(

        UUID productId,

        Integer requestedQuantity,

        Integer availableQuantity,

        String reason

) {
}