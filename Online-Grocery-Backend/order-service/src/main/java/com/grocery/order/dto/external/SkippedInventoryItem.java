package com.grocery.order.dto.external;

import java.util.UUID;

public record SkippedInventoryItem(

        UUID productId,

        Integer requestedQuantity,

        Integer availableQuantity,

        String reason

) {
}