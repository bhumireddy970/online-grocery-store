package com.grocery.order.dto;

import java.util.UUID;

public record OrderItemRequest(

        UUID productId,

        Integer quantity

) {
}