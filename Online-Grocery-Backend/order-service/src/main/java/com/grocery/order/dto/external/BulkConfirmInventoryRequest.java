package com.grocery.order.dto.external;

import java.util.List;

public record BulkConfirmInventoryRequest(

        List<ConfirmInventoryRequest> items

) {
}