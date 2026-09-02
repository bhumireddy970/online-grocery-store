package com.grocery.order.dto.external;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(

        UUID id,

        String sku,

        String name,

        String description,

        BigDecimal price,

        Boolean active,

        UUID categoryId,

        String categoryName,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
