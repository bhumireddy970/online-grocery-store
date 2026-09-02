package com.grocery.order.dto.external;

import java.util.UUID;

public record ReserveInventoryRequest(

        UUID productId,

        Integer quantity

) {
}