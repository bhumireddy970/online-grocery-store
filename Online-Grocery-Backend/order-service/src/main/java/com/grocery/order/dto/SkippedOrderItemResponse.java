package com.grocery.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkippedOrderItemResponse {

    private UUID productId;

    private String productName;

    private Integer requestedQuantity;

    private Integer availableQuantity;

    private String reason;

}