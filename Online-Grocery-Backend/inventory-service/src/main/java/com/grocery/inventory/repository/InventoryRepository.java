package com.grocery.inventory.repository;

import com.grocery.inventory.entity.Inventory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductId(UUID productId);

    boolean existsByProductId(@NotNull(message = "Product Id is required") UUID productId);

    List<Inventory> findByProductIdIn(List<UUID> productIds);
}