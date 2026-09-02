package com.grocery.order.dto.external;


import java.util.UUID;

public record ConfirmInventoryRequest(
        UUID productId,

        Integer quantity) {

}
