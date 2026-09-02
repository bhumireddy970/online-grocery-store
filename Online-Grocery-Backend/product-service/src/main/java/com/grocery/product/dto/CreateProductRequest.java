package com.grocery.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;


public record CreateProductRequest (

    @NotBlank(message = "SKU is required")
     String sku,

    @NotBlank(message = "Product name is required")
     String name,

     String description,

    @Positive(message = "Price must be greater than zero")
     BigDecimal price,

    @NotNull(message = "Active status is required")
     Boolean active,

    @NotNull(message = "Category Id is required")
    UUID categoryId,

    @NotNull(message = "Quantity is required")
     Integer initialQuantity

){}
