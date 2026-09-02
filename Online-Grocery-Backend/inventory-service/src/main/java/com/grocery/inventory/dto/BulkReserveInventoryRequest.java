package com.grocery.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BulkReserveInventoryRequest(

        @NotEmpty
        @Valid
        List<ReserveInventoryRequest> items

) {
}
