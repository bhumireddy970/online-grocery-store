package com.grocery.product.client;

import com.grocery.product.client.dto.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @GetMapping("/api/inventory/product/{productId}/can-delete")
    Boolean canDeleteProduct(@PathVariable UUID productId);

    @DeleteMapping("/api/inventory/product/{productId}")
    void deleteInventory(@PathVariable UUID productId);

    @PostMapping("/api/inventory/initialize")
    void addInventory(@RequestBody InventoryRequest request);
}