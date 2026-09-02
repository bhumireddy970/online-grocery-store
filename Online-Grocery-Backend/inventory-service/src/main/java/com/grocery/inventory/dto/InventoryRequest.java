package com.grocery.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InventoryRequest(

        @NotNull(message = "Product Id is required")
        UUID productId,

        @NotNull(message = "Available quantity is required")
        @Min(value = 0, message = "Available quantity cannot be negative")
        Integer availableQuantity

) {
}