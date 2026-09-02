package com.grocery.order.dto;

import com.grocery.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID customerId,
        OrderStatus status,
        LocalDateTime orderDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        List<SkippedOrderItemResponse> skippedItems
) {}
