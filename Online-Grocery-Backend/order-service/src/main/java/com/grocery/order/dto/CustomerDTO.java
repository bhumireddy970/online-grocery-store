package com.grocery.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CustomerDTO(

        UUID id,

        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        @Size(max = 100)
        String email,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address,

        @Size(max = 255)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @Size(max = 255)
        String role





) {
}