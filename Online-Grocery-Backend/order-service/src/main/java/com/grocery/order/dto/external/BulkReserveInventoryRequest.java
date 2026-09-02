package com.grocery.order.dto.external;

import java.util.List;

public record BulkReserveInventoryRequest(

        List<ReserveInventoryRequest> items

) {
}