package com.grocery.inventory.service.impl;

import com.grocery.inventory.client.ProductClient;
import com.grocery.inventory.dto.*;
import com.grocery.inventory.entity.Inventory;
import com.grocery.inventory.entity.StockMovement;
import com.grocery.inventory.exception.DuplicateInventoryException;
import com.grocery.inventory.exception.InsufficientStockException;
import com.grocery.inventory.exception.InventoryNotFoundException;
import com.grocery.inventory.repository.InventoryRepository;
import com.grocery.inventory.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private UUID productId;

    private Inventory inventory;

    private InventoryRequest request;

    @BeforeEach
    void setUp() {

        productId = UUID.randomUUID();

        inventory = new Inventory();

        inventory.setId(UUID.randomUUID());

        inventory.setProductId(productId);

        inventory.setAvailableQuantity(100);

        inventory.setReservedQuantity(0);

        request = new InventoryRequest(

                productId,

                100

        );

    }

    @Test
    @DisplayName("Should add inventory when inventory does not exist and product exists")
    void givenValidInventoryRequestWhenAddInventoryThenReturnInventoryResponse() {

        when(inventoryRepository.existsByProductId(productId))
                .thenReturn(false);

        when(productClient.existsProduct(productId))
                .thenReturn(true);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.addInventory(request);

        assertThat(response).isNotNull();

        assertThat(response.productId())
                .isEqualTo(productId);

        assertThat(response.availableQuantity())
                .isEqualTo(100);

        assertThat(response.reservedQuantity())
                .isZero();

        verify(inventoryRepository)
                .existsByProductId(productId);

        verify(productClient)
                .existsProduct(productId);

        verify(inventoryRepository)
                .save(any(Inventory.class));

        verify(stockMovementRepository)
                .save(any(StockMovement.class));

    }

    @Test
    @DisplayName("Should throw DuplicateInventoryException when inventory already exists")
    void givenExistingInventoryWhenAddInventoryThenThrowDuplicateInventoryException() {

        when(inventoryRepository.existsByProductId(productId))
                .thenReturn(true);

        assertThatThrownBy(() ->
                inventoryService.addInventory(request))

                .isInstanceOf(DuplicateInventoryException.class)

                .hasMessage("Inventory already exists");

        verify(inventoryRepository)
                .existsByProductId(productId);

        verifyNoInteractions(productClient);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should return all inventory records when inventory exists")
    void givenExistingInventoryWhenGetAllInventoryThenReturnInventoryList() {

        Inventory secondInventory = new Inventory();

        secondInventory.setId(UUID.randomUUID());

        secondInventory.setProductId(UUID.randomUUID());

        secondInventory.setAvailableQuantity(50);

        secondInventory.setReservedQuantity(10);

        when(inventoryRepository.findAll())
                .thenReturn(List.of(inventory, secondInventory));

        List<InventoryResponse> response =
                inventoryService.getAllInventory();

        assertThat(response)
                .hasSize(2);

        assertThat(response)
                .extracting(InventoryResponse::availableQuantity)
                .containsExactly(100, 50);

        verify(inventoryRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should return empty list when inventory does not exist")
    void givenNoInventoryWhenGetAllInventoryThenReturnEmptyList() {

        when(inventoryRepository.findAll())
                .thenReturn(List.of());

        List<InventoryResponse> response =
                inventoryService.getAllInventory();

        assertThat(response)
                .isEmpty();

        verify(inventoryRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should return inventory when product inventory exists")
    void givenExistingProductIdWhenGetInventoryByProductThenReturnInventoryResponse() {

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response =
                inventoryService.getInventoryByProduct(productId);

        assertThat(response).isNotNull();

        assertThat(response.productId())
                .isEqualTo(productId);

        assertThat(response.availableQuantity())
                .isEqualTo(100);

        assertThat(response.reservedQuantity())
                .isZero();

        verify(inventoryRepository)
                .findByProductId(productId);

    }

    @Test
    @DisplayName("Should throw InventoryNotFoundException when inventory does not exist")
    void givenInvalidProductIdWhenGetInventoryByProductThenThrowInventoryNotFoundException() {

        UUID invalidProductId = UUID.randomUUID();

        when(inventoryRepository.findByProductId(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.getInventoryByProduct(invalidProductId))

                .isInstanceOf(InventoryNotFoundException.class)

                .hasMessage("Inventory not found");

        verify(inventoryRepository)
                .findByProductId(invalidProductId);

    }

    @Test
    @DisplayName("Should reserve inventory when sufficient stock is available")
    void givenSufficientAvailableQuantityWhenReserveInventoryThenReturnUpdatedInventoryResponse() {

        ReserveInventoryRequest request =
                new ReserveInventoryRequest(
                        productId,
                        20);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response =
                inventoryService.reserveInventory(request);

        assertThat(response).isNotNull();

        assertThat(response.availableQuantity())
                .isEqualTo(80);

        assertThat(response.reservedQuantity())
                .isEqualTo(20);

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository)
                .save(any(Inventory.class));

        verify(stockMovementRepository)
                .save(any(StockMovement.class));

    }

    @Test
    @DisplayName("Should throw InventoryNotFoundException when reserving inventory for non existing product")
    void givenInvalidProductIdWhenReserveInventoryThenThrowInventoryNotFoundException() {

        UUID invalidProductId = UUID.randomUUID();

        ReserveInventoryRequest request =
                new ReserveInventoryRequest(
                        invalidProductId,
                        10);

        when(inventoryRepository.findByProductId(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.reserveInventory(request))

                .isInstanceOf(InventoryNotFoundException.class)

                .hasMessage("Inventory not found");

        verify(inventoryRepository)
                .findByProductId(invalidProductId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should throw InsufficientStockException when available quantity is insufficient")
    void givenInsufficientAvailableQuantityWhenReserveInventoryThenThrowInsufficientStockException() {

        ReserveInventoryRequest request =
                new ReserveInventoryRequest(
                        productId,
                        150);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        assertThatThrownBy(() ->
                inventoryService.reserveInventory(request))

                .isInstanceOf(InsufficientStockException.class)

                .hasMessage("Insufficient stock");

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should release inventory when reserved quantity is sufficient")
    void givenSufficientReservedQuantityWhenReleaseInventoryThenReturnUpdatedInventoryResponse() {

        inventory.setAvailableQuantity(80);
        inventory.setReservedQuantity(20);

        ReleaseInventoryRequest request =
                new ReleaseInventoryRequest(
                        productId,
                        10);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response =
                inventoryService.releaseInventory(request);

        assertThat(response).isNotNull();

        assertThat(response.availableQuantity())
                .isEqualTo(90);

        assertThat(response.reservedQuantity())
                .isEqualTo(10);

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository)
                .save(any(Inventory.class));

        verify(stockMovementRepository)
                .save(any(StockMovement.class));

    }

    @Test
    @DisplayName("Should throw InventoryNotFoundException when releasing inventory for non existing product")
    void givenInvalidProductIdWhenReleaseInventoryThenThrowInventoryNotFoundException() {

        UUID invalidProductId = UUID.randomUUID();

        ReleaseInventoryRequest request =
                new ReleaseInventoryRequest(
                        invalidProductId,
                        10);

        when(inventoryRepository.findByProductId(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.releaseInventory(request))

                .isInstanceOf(InventoryNotFoundException.class)

                .hasMessage("Inventory not found");

        verify(inventoryRepository)
                .findByProductId(invalidProductId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should throw InsufficientStockException when reserved quantity is insufficient")
    void givenInsufficientReservedQuantityWhenReleaseInventoryThenThrowInsufficientStockException() {

        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(5);

        ReleaseInventoryRequest request =
                new ReleaseInventoryRequest(
                        productId,
                        10);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        assertThatThrownBy(() ->
                inventoryService.releaseInventory(request))

                .isInstanceOf(InsufficientStockException.class)

                .hasMessage("Reserved quantity is insufficient");

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should confirm inventory when reserved quantity is sufficient")
    void givenSufficientReservedQuantityWhenConfirmInventoryThenReturnUpdatedInventoryResponse() {

        inventory.setAvailableQuantity(80);
        inventory.setReservedQuantity(20);

        ConfirmInventoryRequest request =
                new ConfirmInventoryRequest(
                        productId,
                        10);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response =
                inventoryService.confirmInventory(request);

        assertThat(response).isNotNull();

        assertThat(response.availableQuantity())
                .isEqualTo(80);

        assertThat(response.reservedQuantity())
                .isEqualTo(10);

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository)
                .save(any(Inventory.class));

        verify(stockMovementRepository)
                .save(any(StockMovement.class));

    }

    @Test
    @DisplayName("Should throw InventoryNotFoundException when confirming inventory for non existing product")
    void givenInvalidProductIdWhenConfirmInventoryThenThrowInventoryNotFoundException() {

        UUID invalidProductId = UUID.randomUUID();

        ConfirmInventoryRequest request =
                new ConfirmInventoryRequest(
                        invalidProductId,
                        10);

        when(inventoryRepository.findByProductId(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.confirmInventory(request))

                .isInstanceOf(InventoryNotFoundException.class)

                .hasMessage("Inventory not found");

        verify(inventoryRepository)
                .findByProductId(invalidProductId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should throw InsufficientStockException when confirming inventory with insufficient reserved quantity")
    void givenInsufficientReservedQuantityWhenConfirmInventoryThenThrowInsufficientStockException() {

        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(5);

        ConfirmInventoryRequest request =
                new ConfirmInventoryRequest(
                        productId,
                        10);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        assertThatThrownBy(() ->
                inventoryService.confirmInventory(request))

                .isInstanceOf(InsufficientStockException.class)

                .hasMessage("Reserved quantity is insufficient");

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should delete inventory when inventory exists")
    void givenExistingInventoryWhenDeleteInventoryThenDeleteInventory() {

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.deleteInventory(productId);

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository)
                .delete(inventory);

    }

    @Test
    @DisplayName("Should not delete inventory when inventory does not exist")
    void givenNonExistingInventoryWhenDeleteInventoryThenDoNothing() {

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        inventoryService.deleteInventory(productId);

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository, never())
                .delete(any());

    }

    @Test
    @DisplayName("Should increase available quantity when restocking inventory")
    void givenExistingInventoryWhenRestockInventoryThenReturnUpdatedInventoryResponse() {

        RestockInventoryRequest request =
                new RestockInventoryRequest(
                        productId,
                        50);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response =
                inventoryService.restockInventory(request);

        assertThat(response.availableQuantity())
                .isEqualTo(150);

        assertThat(response.reservedQuantity())
                .isZero();

        verify(inventoryRepository)
                .findByProductId(productId);

        verify(inventoryRepository)
                .save(any(Inventory.class));

        verify(stockMovementRepository)
                .save(any(StockMovement.class));

    }

    @Test
    @DisplayName("Should throw InventoryNotFoundException when restocking non existing inventory")
    void givenInvalidProductIdWhenRestockInventoryThenThrowInventoryNotFoundException() {

        UUID invalidProductId = UUID.randomUUID();

        RestockInventoryRequest request =
                new RestockInventoryRequest(
                        invalidProductId,
                        20);

        when(inventoryRepository.findByProductId(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryService.restockInventory(request))

                .isInstanceOf(InventoryNotFoundException.class)

                .hasMessage("Inventory not found");

        verify(inventoryRepository)
                .findByProductId(invalidProductId);

        verify(inventoryRepository, never())
                .save(any());

        verify(stockMovementRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should reserve inventory for all products when sufficient stock is available")
    void givenSufficientStockForAllProductsWhenReserveBulkInventoryThenReserveAllProducts() {

        UUID productIdOne = UUID.randomUUID();
        UUID productIdTwo = UUID.randomUUID();

        Inventory inventoryOne = new Inventory();
        inventoryOne.setProductId(productIdOne);
        inventoryOne.setAvailableQuantity(50);
        inventoryOne.setReservedQuantity(0);

        Inventory inventoryTwo = new Inventory();
        inventoryTwo.setProductId(productIdTwo);
        inventoryTwo.setAvailableQuantity(100);
        inventoryTwo.setReservedQuantity(10);

        BulkReserveInventoryRequest request =
                new BulkReserveInventoryRequest(

                        List.of(

                                new ReserveInventoryRequest(productIdOne,10),

                                new ReserveInventoryRequest(productIdTwo,20)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of(inventoryOne, inventoryTwo));

        BulkReserveInventoryResponse response =
                inventoryService.reserveBulkInventory(request);

        assertThat(response.reservedItems())
                .hasSize(2);

        assertThat(response.skippedItems())
                .isEmpty();

        assertThat(inventoryOne.getAvailableQuantity())
                .isEqualTo(40);

        assertThat(inventoryOne.getReservedQuantity())
                .isEqualTo(10);

        assertThat(inventoryTwo.getAvailableQuantity())
                .isEqualTo(80);

        assertThat(inventoryTwo.getReservedQuantity())
                .isEqualTo(30);

        verify(inventoryRepository)
                .saveAll(any());

        verify(stockMovementRepository)
                .saveAll(any());

    }

    @Test
    @DisplayName("Should reserve available products and skip unavailable products")
    void givenMixedInventoryAvailabilityWhenReserveBulkInventoryThenReserveAvailableProductsOnly() {

        UUID productIdOne = UUID.randomUUID();

        UUID productIdTwo = UUID.randomUUID();

        Inventory inventory = new Inventory();

        inventory.setProductId(productIdOne);

        inventory.setAvailableQuantity(30);

        inventory.setReservedQuantity(0);

        BulkReserveInventoryRequest request =
                new BulkReserveInventoryRequest(

                        List.of(

                                new ReserveInventoryRequest(productIdOne,10),

                                new ReserveInventoryRequest(productIdTwo,15)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of(inventory));

        BulkReserveInventoryResponse response =
                inventoryService.reserveBulkInventory(request);

        assertThat(response.reservedItems())
                .hasSize(1);

        assertThat(response.skippedItems())
                .hasSize(1);

        assertThat(response.skippedItems().get(0).reason())
                .isEqualTo("Inventory not found");

        verify(inventoryRepository)
                .saveAll(any());

        verify(stockMovementRepository)
                .saveAll(any());

    }

    @Test
    @DisplayName("Should skip product when inventory does not exist")
    void givenMissingInventoryWhenReserveBulkInventoryThenSkipProduct() {

        UUID productId = UUID.randomUUID();

        BulkReserveInventoryRequest request =
                new BulkReserveInventoryRequest(

                        List.of(

                                new ReserveInventoryRequest(productId,5)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of());

        BulkReserveInventoryResponse response =
                inventoryService.reserveBulkInventory(request);

        assertThat(response.reservedItems())
                .isEmpty();

        assertThat(response.skippedItems())
                .hasSize(1);

        assertThat(response.skippedItems().get(0).reason())
                .isEqualTo("Inventory not found");

        verify(inventoryRepository)
                .saveAll(any());

    }



    @Test
    @DisplayName("Should release inventory for all products")
    void givenSufficientReservedQuantityWhenReleaseBulkInventoryThenReturnUpdatedInventoryList() {

        inventory.setAvailableQuantity(60);

        inventory.setReservedQuantity(40);

        BulkReleaseInventoryRequest request =
                new BulkReleaseInventoryRequest(

                        List.of(

                                new ReleaseInventoryRequest(
                                        productId,
                                        20)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of(inventory));

        List<InventoryResponse> response =
                inventoryService.releaseBulkInventory(request);

        assertThat(response)
                .hasSize(1);

        assertThat(response.get(0).availableQuantity())
                .isEqualTo(80);

        assertThat(response.get(0).reservedQuantity())
                .isEqualTo(20);

        verify(inventoryRepository)
                .saveAll(anyCollection());

    }

    @Test
    @DisplayName("Should throw InsufficientStockException when reserved quantity is insufficient during bulk release")
    void givenInsufficientReservedQuantityWhenReleaseBulkInventoryThenThrowInsufficientStockException() {

        inventory.setAvailableQuantity(100);

        inventory.setReservedQuantity(5);

        BulkReleaseInventoryRequest request =
                new BulkReleaseInventoryRequest(

                        List.of(

                                new ReleaseInventoryRequest(
                                        productId,
                                        10)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of(inventory));

        assertThatThrownBy(() ->
                inventoryService.releaseBulkInventory(request))

                .isInstanceOf(InsufficientStockException.class);

    }

    @Test
    @DisplayName("Should confirm inventory for all products")
    void givenSufficientReservedQuantityWhenConfirmBulkInventoryThenReturnUpdatedInventoryList() {

        inventory.setAvailableQuantity(60);

        inventory.setReservedQuantity(40);

        BulkConfirmInventoryRequest request =
                new BulkConfirmInventoryRequest(

                        List.of(

                                new ConfirmInventoryRequest(
                                        productId,
                                        20)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of(inventory));

        List<InventoryResponse> response =
                inventoryService.confirmBulkInventory(request);

        assertThat(response)
                .hasSize(1);

        assertThat(response.get(0).availableQuantity())
                .isEqualTo(60);

        assertThat(response.get(0).reservedQuantity())
                .isEqualTo(20);

        verify(inventoryRepository)
                .saveAll(anyCollection());

    }

    @Test
    @DisplayName("Should throw InsufficientStockException when reserved quantity is insufficient during bulk confirmation")
    void givenInsufficientReservedQuantityWhenConfirmBulkInventoryThenThrowInsufficientStockException() {

        inventory.setAvailableQuantity(60);

        inventory.setReservedQuantity(5);

        BulkConfirmInventoryRequest request =
                new BulkConfirmInventoryRequest(

                        List.of(

                                new ConfirmInventoryRequest(
                                        productId,
                                        10)

                        )

                );

        when(inventoryRepository.findByProductIdIn(any()))
                .thenReturn(List.of(inventory));

        assertThatThrownBy(() ->
                inventoryService.confirmBulkInventory(request))

                .isInstanceOf(InsufficientStockException.class);

    }
}

