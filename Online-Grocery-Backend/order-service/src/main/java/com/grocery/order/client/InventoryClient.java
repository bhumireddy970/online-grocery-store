package com.grocery.order.client;

import com.grocery.order.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "INVENTORY-SERVICE", url = "http://localhost:8080")
public interface InventoryClient {

    @GetMapping("/api/inventory/product/{productId}")
    InventoryResponse getInventory(@PathVariable UUID productId);

    @PostMapping("/api/inventory/reserve")
    InventoryResponse reserveInventory(
            @RequestBody ReserveInventoryRequest request);

    @PostMapping("/api/inventory/release")
    InventoryResponse releaseInventory(
            @RequestBody ReleaseInventoryRequest request);

    @PostMapping("/api/inventory/confirm")
    InventoryResponse confirmInventory(
            @RequestBody ConfirmInventoryRequest request);

    @PostMapping("/api/inventory/reserve-bulk")
    BulkReserveInventoryResponse reserveBulkInventory(
            BulkReserveInventoryRequest request);

    @PostMapping("/api/inventory/release-bulk")
    List<InventoryResponse> releaseBulkInventory(
            @RequestBody BulkReleaseInventoryRequest request);

    @PostMapping("/api/inventory/confirm-bulk")
    List<InventoryResponse> confirmBulkInventory(
            @RequestBody BulkConfirmInventoryRequest request);

}
