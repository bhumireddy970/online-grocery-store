package com.grocery.inventory.service.impl;

import com.grocery.inventory.client.ProductClient;
import com.grocery.inventory.dto.*;
import com.grocery.inventory.entity.Inventory;
import com.grocery.inventory.entity.MovementType;
import com.grocery.inventory.entity.StockMovement;
import com.grocery.inventory.exception.DuplicateInventoryException;
import com.grocery.inventory.exception.InsufficientStockException;
import com.grocery.inventory.exception.InventoryNotFoundException;
import com.grocery.inventory.exception.ProductNotFoundException;
import com.grocery.inventory.repository.InventoryRepository;
import com.grocery.inventory.repository.StockMovementRepository;
import com.grocery.inventory.service.InventoryService;
import com.grocery.inventory.dto.BulkReserveInventoryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final StockMovementRepository stockMovementRepository;

    private final ProductClient productClient;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                StockMovementRepository stockMovementRepository, ProductClient productClient) {

        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productClient = productClient;
    }

    @Override
    public InventoryResponse addInventory(InventoryRequest request) {

        if (inventoryRepository.existsByProductId(request.productId())) {
            throw new DuplicateInventoryException("Inventory already exists");
        }

        Inventory inventory = new Inventory();

        inventory.setProductId(request.productId());
        inventory.setAvailableQuantity(request.availableQuantity());
        inventory.setReservedQuantity(0);

        if (!productClient.existsProduct(request.productId())) {

            throw new ProductNotFoundException(
                    "Product not found.");

        }

        Inventory savedInventory = inventoryRepository.save(inventory);

        saveMovement(
                savedInventory.getProductId(),
                savedInventory.getAvailableQuantity(),
                MovementType.IN
        );

        return mapToResponse(savedInventory);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InventoryResponse getInventoryByProduct(UUID productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found"));

        return mapToResponse(inventory);
    }


    @Override
    public InventoryResponse reserveInventory(ReserveInventoryRequest request) {

        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found"));

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - request.quantity());

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + request.quantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        saveMovement(
                inventory.getProductId(),
                request.quantity(),
                MovementType.RESERVE
        );

        return mapToResponse(updatedInventory);
    }


    @Override
    public InventoryResponse releaseInventory(ReleaseInventoryRequest request) {

        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found"));

        if (inventory.getReservedQuantity() < request.quantity()) {
            throw new InsufficientStockException("Reserved quantity is insufficient");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.quantity());

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + request.quantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        saveMovement(
                inventory.getProductId(),
                request.quantity(),
                MovementType.RELEASE
        );

        return mapToResponse(updatedInventory);
    }

    @Override
    public InventoryResponse confirmInventory(ConfirmInventoryRequest request) {

        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found"));

        if (inventory.getReservedQuantity() < request.quantity()) {
            throw new InsufficientStockException("Reserved quantity is insufficient");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.quantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        saveMovement(
                inventory.getProductId(),
                request.quantity(),
                MovementType.OUT
        );

        return mapToResponse(updatedInventory);
    }

   @Override
public Boolean canDeleteProduct(UUID productId) {

    Inventory inventory = inventoryRepository
            .findByProductId(productId)
            .orElse(null);

    if (inventory == null) {
        return true;
    }

    return inventory.getAvailableQuantity() == 0
            && inventory.getReservedQuantity() == 0;
}


    @Override
    public void deleteInventory(UUID productId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElse(null);

        if (inventory != null) {
            inventoryRepository.delete(inventory);
        }
    }

    @Override
    public InventoryResponse restockInventory(RestockInventoryRequest request) {

        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found"));

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + request.quantity());

        Inventory saved = inventoryRepository.save(inventory);

        saveMovement(
                request.productId(),
                request.quantity(),
                MovementType.IN
        );

        return mapToResponse(saved);
    }

    private InventoryResponse mapToResponse(Inventory inventory) {

        return new InventoryResponse(

                inventory.getProductId(),

                inventory.getAvailableQuantity(),

                inventory.getReservedQuantity(),

                inventory.getCreatedAt(),

                inventory.getUpdatedAt()

        );

    }

    private void saveMovement(UUID productId,
                              Integer quantity,
                              MovementType movementType) {

        StockMovement movement = new StockMovement();

        movement.setProductId(productId);

        movement.setQuantity(quantity);

        movement.setMovementType(movementType);

        movement.setMovementDate(LocalDateTime.now());

        stockMovementRepository.save(movement);

    }

    @Transactional
    @Override
    public BulkReserveInventoryResponse reserveBulkInventory(
            BulkReserveInventoryRequest request) {

        List<UUID> productIds = request.items()
                .stream()
                .map(ReserveInventoryRequest::productId)
                .toList();

        List<Inventory> inventories =
                inventoryRepository.findByProductIdIn(productIds);

        Map<UUID, Inventory> inventoryMap =
                inventories.stream()
                        .collect(Collectors.toMap(
                                Inventory::getProductId,
                                Function.identity()));

        List<ReservedInventoryItem> reservedItems = new ArrayList<>();

        List<SkippedInventoryItem> skippedItems = new ArrayList<>();

        List<Inventory> inventoriesToUpdate = new ArrayList<>();

        List<StockMovement> movements = new ArrayList<>();

        for (ReserveInventoryRequest item : request.items()) {

            Inventory inventory = inventoryMap.get(item.productId());

            if (inventory == null) {

                skippedItems.add(

                        new SkippedInventoryItem(

                                item.productId(),

                                item.quantity(),

                                0,

                                "Inventory not found"

                        )

                );

                continue;

            }

            if (inventory.getAvailableQuantity() < item.quantity()) {

                skippedItems.add(

                        new SkippedInventoryItem(

                                item.productId(),

                                item.quantity(),

                                inventory.getAvailableQuantity(),

                                "Insufficient stock"

                        )

                );

                continue;

            }

            inventory.setAvailableQuantity(

                    inventory.getAvailableQuantity()
                            - item.quantity()

            );

            inventory.setReservedQuantity(

                    inventory.getReservedQuantity()
                            + item.quantity()

            );

            reservedItems.add(

                    new ReservedInventoryItem(

                            item.productId(),

                            item.quantity()

                    )

            );

            inventoriesToUpdate.add(inventory);

            StockMovement movement = new StockMovement();

            movement.setProductId(item.productId());

            movement.setQuantity(item.quantity());

            movement.setMovementType(MovementType.RESERVE);

            movement.setMovementDate(LocalDateTime.now());

            movements.add(movement);

        }

        inventoryRepository.saveAll(inventoriesToUpdate);

        stockMovementRepository.saveAll(movements);

        return new BulkReserveInventoryResponse(

                reservedItems,

                skippedItems

        );

    }

    @Override
    @Transactional
    public List<InventoryResponse> releaseBulkInventory(
            BulkReleaseInventoryRequest request) {

        List<UUID> productIds = request.items()
                .stream()
                .map(ReleaseInventoryRequest::productId)
                .toList();

        Map<UUID, Inventory> inventoryMap =
                inventoryRepository.findByProductIdIn(productIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Inventory::getProductId,
                                Function.identity()));


        for (ReleaseInventoryRequest item : request.items()) {

            Inventory inventory = inventoryMap.get(item.productId());

            if (inventory == null) {
                throw new InventoryNotFoundException(
                        "Inventory not found for product : " + item.productId());
            }

            if (inventory.getReservedQuantity() < item.quantity()) {
                throw new InsufficientStockException(
                        "Reserved quantity is insufficient for product : " + item.productId());
            }
        }


        for (ReleaseInventoryRequest item : request.items()) {

            Inventory inventory = inventoryMap.get(item.productId());

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() - item.quantity());

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() + item.quantity());

            saveMovement(
                    item.productId(),
                    item.quantity(),
                    MovementType.RELEASE);
        }

        inventoryRepository.saveAll(inventoryMap.values());

        return inventoryMap.values()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<InventoryResponse> confirmBulkInventory(
            BulkConfirmInventoryRequest request) {

        List<UUID> productIds = request.items()
                .stream()
                .map(ConfirmInventoryRequest::productId)
                .toList();

        Map<UUID, Inventory> inventoryMap =
                inventoryRepository.findByProductIdIn(productIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Inventory::getProductId,
                                Function.identity()));


        for (ConfirmInventoryRequest item : request.items()) {

            Inventory inventory = inventoryMap.get(item.productId());

            if (inventory == null) {
                throw new InventoryNotFoundException(
                        "Inventory not found for product : " + item.productId());
            }

            if (inventory.getReservedQuantity() < item.quantity()) {
                throw new InsufficientStockException(
                        "Reserved quantity is insufficient for product : "
                                + item.productId());
            }
        }


        for (ConfirmInventoryRequest item : request.items()) {

            Inventory inventory = inventoryMap.get(item.productId());

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() - item.quantity());

            saveMovement(
                    item.productId(),
                    item.quantity(),
                    MovementType.OUT);
        }

        inventoryRepository.saveAll(inventoryMap.values());

        return inventoryMap.values()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

}
