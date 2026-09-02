package com.grocery.inventory.repository;

import com.grocery.inventory.entity.MovementType;
import com.grocery.inventory.entity.StockMovement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StockMovementRepositoryTest {

    @Autowired private StockMovementRepository stockMovementRepository;

    @Test
    void shouldSaveStockMovement() {
        StockMovement movement = new StockMovement();
        movement.setProductId(UUID.randomUUID());
        movement.setQuantity(10);
        movement.setMovementType(MovementType.IN);
        movement.setMovementDate(LocalDateTime.now());

        StockMovement saved = stockMovementRepository.save(movement);

        assertThat(saved.getId()).isNotNull();
    }
}
