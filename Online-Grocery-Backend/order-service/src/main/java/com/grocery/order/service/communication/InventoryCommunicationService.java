package com.grocery.order.service.communication;

import com.grocery.order.client.InventoryClient;
import com.grocery.order.dto.external.*;
import com.grocery.order.exception.InventoryServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryCommunicationService {

    private final InventoryClient inventoryClient;

    public InventoryCommunicationService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "inventoryFallback")
    @Retry(name = "inventoryService")
    public InventoryResponse getInventory(UUID productId) {

        return inventoryClient.getInventory(productId);

    }

    public InventoryResponse inventoryFallback(
            Long productId,
            Exception ex) {

        System.out.println("Inventory Service Circuit Breaker Activated");

        throw new InventoryServiceException(
                "Inventory Service is currently unavailable");

    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "reserveFallback")
    @Retry(name = "inventoryService")
    public InventoryResponse reserveInventory(
            ReserveInventoryRequest request) {

        return inventoryClient.reserveInventory(request);

    }

    public InventoryResponse reserveFallback(
            ReserveInventoryRequest request,
            Exception ex) {

        System.out.println("Reserve Inventory Fallback Executed");

        throw new InventoryServiceException(
                "Unable to reserve inventory");

    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "releaseFallback")
    @Retry(name = "inventoryService")
    public InventoryResponse releaseInventory(
            ReleaseInventoryRequest request) {

        return inventoryClient.releaseInventory(request);

    }

    public InventoryResponse releaseFallback(
            ReleaseInventoryRequest request,
            Exception ex) {

        System.out.println("Release Inventory Fallback Executed");

        throw new InventoryServiceException(
                "Unable to release inventory");

    }



    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "confirmFallback")
    @Retry(name = "inventoryService")
    public InventoryResponse confirmInventory(
            ConfirmInventoryRequest request) {

        return inventoryClient.confirmInventory(request);

    }

    public InventoryResponse confirmFallback(
            ConfirmInventoryRequest request,
            Exception ex) {

        System.out.println("Confirm Inventory Fallback Executed");

        throw new InventoryServiceException(
                "Unable to confirm inventory");

    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "reserveBulkFallback")
    @Retry(name = "inventoryService")
    public BulkReserveInventoryResponse reserveBulkInventory(
            BulkReserveInventoryRequest request) {

        return inventoryClient.reserveBulkInventory(request);
    }

    public BulkReserveInventoryResponse reserveBulkFallback(
            BulkReserveInventoryRequest request,
            Exception ex) {

        throw new InventoryServiceException(
                "Unable to reserve inventory.");
    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "releaseBulkFallback")
    @Retry(name = "inventoryService")
    public List<InventoryResponse> releaseBulkInventory(

            BulkReleaseInventoryRequest request){

        return inventoryClient.releaseBulkInventory(request);

    }

    public List<InventoryResponse> releaseBulkFallback(

            BulkReleaseInventoryRequest request,

            Exception ex){

        throw new InventoryServiceException(

                "Unable to release inventory."

        );

    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "confirmBulkFallback")
    @Retry(name = "inventoryService")
    public List<InventoryResponse> confirmBulkInventory(

            BulkConfirmInventoryRequest request){

        return inventoryClient.confirmBulkInventory(request);

    }

    public List<InventoryResponse> confirmBulkFallback(

            BulkConfirmInventoryRequest request,

            Exception ex){

        throw new InventoryServiceException(

                "Unable to confirm inventory."

        );

    }

}