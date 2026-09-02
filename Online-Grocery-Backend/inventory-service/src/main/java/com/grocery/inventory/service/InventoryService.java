package com.grocery.inventory.service;

import com.grocery.inventory.dto.*;
import com.grocery.inventory.dto.BulkReserveInventoryRequest;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    InventoryResponse addInventory(InventoryRequest request);

    List<InventoryResponse> getAllInventory();

    InventoryResponse getInventoryByProduct(UUID productId);

    InventoryResponse reserveInventory(ReserveInventoryRequest request);

    InventoryResponse releaseInventory(ReleaseInventoryRequest request);

    InventoryResponse confirmInventory(ConfirmInventoryRequest request);

    Boolean canDeleteProduct(UUID productId);

    void deleteInventory(UUID productId);

    InventoryResponse restockInventory(RestockInventoryRequest request);

    BulkReserveInventoryResponse reserveBulkInventory(
            BulkReserveInventoryRequest request);

    List<InventoryResponse> releaseBulkInventory(
            BulkReleaseInventoryRequest request);

    List<InventoryResponse> confirmBulkInventory(
            BulkConfirmInventoryRequest request);
}