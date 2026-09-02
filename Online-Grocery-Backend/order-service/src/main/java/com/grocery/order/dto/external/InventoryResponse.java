package com.grocery.order.dto.external;


import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID productId,

        Integer availableQuantity,

        Integer reservedQuantity,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {


    public static record SkippedInventoryItem(

            UUID productId,

            Integer requestedQuantity,

            Integer availableQuantity,

            String reason

    ) {
    }
}
