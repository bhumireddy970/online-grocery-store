package com.grocery.order.dto.external;

import java.util.List;

public record BulkReleaseInventoryRequest(

        List<ReleaseInventoryRequest> items

) {
}