package com.grocery.order.dto.external;

import java.util.UUID;

public record ReservedInventoryItem(

        UUID productId,

        Integer quantity

) {
}