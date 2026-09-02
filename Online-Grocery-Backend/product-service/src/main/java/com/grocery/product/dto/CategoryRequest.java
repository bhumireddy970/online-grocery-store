package com.grocery.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        @Schema(description = "Category Request" ,example="Vegetables" )
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        @Schema(description = "Category Request" ,example="description " )
        String description

) {}