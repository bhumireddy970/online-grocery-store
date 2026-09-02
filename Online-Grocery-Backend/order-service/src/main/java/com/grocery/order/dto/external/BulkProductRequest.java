package com.grocery.order.dto.external;

import java.util.List;
import java.util.UUID;

public record BulkProductRequest(

        List<UUID> productIds

) {
}