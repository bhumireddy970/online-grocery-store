package com.grocery.order.dto.external;

import java.util.List;

public record BulkReserveInventoryResponse(

        List<ReservedInventoryItem> reservedItems,

        List<SkippedInventoryItem> skippedItems

) {
}