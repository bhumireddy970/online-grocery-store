package com.grocery.inventory.dto;

import java.util.UUID;

public record ReservedInventoryItem(

        UUID productId,

        Integer quantity

) {
}