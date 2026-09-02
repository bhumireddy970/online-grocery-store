package com.grocery.inventory.repository;

import com.grocery.inventory.entity.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired private InventoryRepository inventoryRepository;

    @Test
    void shouldSaveAndFindInventoryByProductId() {
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(0);

        inventoryRepository.save(inventory);

        assertThat(inventoryRepository.existsByProductId(productId)).isTrue();
        assertThat(inventoryRepository.findByProductId(productId)).isPresent();
    }
}
