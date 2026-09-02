package com.grocery.order.dto.external;

import java.util.UUID;

public record ReleaseInventoryRequest(

        UUID productId,

        Integer quantity

) {
}