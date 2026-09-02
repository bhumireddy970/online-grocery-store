
package com.grocery.inventory.dto;

import java.util.List;

public record BulkReserveInventoryResponse(

        List<ReservedInventoryItem> reservedItems,

        List<SkippedInventoryItem> skippedItems

) {
}