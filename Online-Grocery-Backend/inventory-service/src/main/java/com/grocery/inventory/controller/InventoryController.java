package com.grocery.inventory.controller;

import com.grocery.inventory.dto.*;
import com.grocery.inventory.exception.ErrorResponse;
import com.grocery.inventory.service.InventoryService;
import com.grocery.inventory.dto.BulkReserveInventoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(
        name = "Inventory Management API",
        description = """
                REST APIs for managing grocery inventory.

                Features
                • Initialize inventory for newly created products
                • Retrieve inventory details
                • Reserve stock during order creation
                • Release reserved stock after order cancellation
                • Confirm stock after successful order
                • Restock products
                • Validate product deletion
                • Delete inventory
                """
)
public class InventoryController {

    private final InventoryService inventoryService;


    @Operation(
            summary = "Initialize Inventory",
            description = """
                    Creates inventory for a newly created product.
                    
                    Business Rules:
                    • Product must exist.
                    • Inventory must not already exist.
                    • Available quantity cannot be negative.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Inventory created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "productId":"550e8400-e29b-41d4-a716-446655440000",
                                              "availableQuantity":100,
                                              "reservedQuantity":0
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid inventory request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":400,
                                              "error":"Bad Request",
                                              "message":"Available quantity cannot be negative",
                                              "path":"/api/inventory/initialize"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":404,
                                              "error":"Not Found",
                                              "message":"Product not found",
                                              "path":"/api/inventory/initialize"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Inventory already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":409,
                                              "error":"Conflict",
                                              "message":"Inventory already exists",
                                              "path":"/api/inventory/initialize"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/initialize")
    public ResponseEntity<InventoryResponse> addInventory(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Inventory initialization request",
                    content = @Content(
                            schema = @Schema(implementation = InventoryRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "productId":"550e8400-e29b-41d4-a716-446655440000",
                                              "availableQuantity":100
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody InventoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.addInventory(request));
    }

    @Operation(
            summary = "Get All Inventory",
            description = "Returns all inventory records."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "productId":"550e8400-e29b-41d4-a716-446655440000",
                                                "availableQuantity":100,
                                                "reservedQuantity":10
                                              },
                                              {
                                                "productId":"6d9d63fd-67d2-4707-a39f-f2c4793a2d73",
                                                "availableQuantity":40,
                                                "reservedQuantity":0
                                              }
                                            ]
                                            """
                            )
                    )
            )

    })
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {

        return ResponseEntity.ok(
                inventoryService.getAllInventory()
        );
    }

    @Operation(
            summary = "Get Inventory By Product ID",
            description = """
                    Retrieves inventory details of a specific product.

                    Returns the available and reserved quantities.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "productId":"550e8400-e29b-41d4-a716-446655440000",
                                      "availableQuantity":85,
                                      "reservedQuantity":15
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Inventory not found",
                                      "path":"/api/inventory/product/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            )

    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(

            @Parameter(
                    description = "Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID productId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProduct(productId));
    }

    @Operation(
            summary = "Reserve Inventory",
            description = """
                    Reserves stock when a customer places an order.

                    Business Rules:
                    • Inventory must exist.
                    • Requested quantity must be available.
                    • Reserved quantity increases.
                    • Available quantity decreases.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory reserved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "productId":"550e8400-e29b-41d4-a716-446655440000",
                                      "availableQuantity":90,
                                      "reservedQuantity":10
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Insufficient stock",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Insufficient stock",
                                      "path":"/api/inventory/reserve"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Inventory not found",
                                      "path":"/api/inventory/reserve"
                                    }
                                    """
                            )
                    )
            )

    })
    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserveInventory(

            @Valid
            @RequestBody ReserveInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.reserveInventory(request));
    }

    @Operation(
            summary = "Release Reserved Inventory",
            description = """
                    Releases previously reserved stock.

                    Usually called when:
                    • Order is cancelled
                    • Payment fails
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Reserved stock released successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "productId":"550e8400-e29b-41d4-a716-446655440000",
                                      "availableQuantity":100,
                                      "reservedQuantity":0
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Reserved quantity is insufficient",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Reserved quantity is insufficient",
                                      "path":"/api/inventory/release"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Inventory not found",
                                      "path":"/api/inventory/release"
                                    }
                                    """
                            )
                    )
            )

    })
    @PostMapping("/release")
    public ResponseEntity<InventoryResponse> releaseInventory(

            @Valid
            @RequestBody ReleaseInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.releaseInventory(request));
    }

    @Operation(
            summary = "Confirm Inventory",
            description = """
                    Confirms reserved inventory after successful order payment.

                    Business Rules:
                    • Inventory must exist.
                    • Reserved quantity must be sufficient.
                    • Reserved quantity decreases permanently.
                    • Stock movement is recorded as OUT.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory confirmed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "productId":"550e8400-e29b-41d4-a716-446655440000",
                                      "availableQuantity":90,
                                      "reservedQuantity":0
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Reserved quantity is insufficient",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Reserved quantity is insufficient",
                                      "path":"/api/inventory/confirm"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Inventory not found",
                                      "path":"/api/inventory/confirm"
                                    }
                                    """
                            )
                    )
            )

    })
    @PostMapping("/confirm")
    public ResponseEntity<InventoryResponse> confirmInventory(

            @Valid
            @RequestBody ConfirmInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.confirmInventory(request));
    }

    @Operation(
            summary = "Restock Inventory",
            description = """
                    Adds additional stock for an existing product.

                    Business Rules:
                    • Inventory must exist.
                    • Quantity must be greater than zero.
                    • Available quantity increases.
                    • Stock movement is recorded as IN.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory restocked successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "productId":"550e8400-e29b-41d4-a716-446655440000",
                                      "availableQuantity":150,
                                      "reservedQuantity":5
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Inventory not found",
                                      "path":"/api/inventory/restock"
                                    }
                                    """
                            )
                    )
            )

    })
    @PostMapping("/restock")
    public ResponseEntity<InventoryResponse> restockInventory(

            @Valid
            @RequestBody RestockInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.restockInventory(request));
    }

    @Operation(
            summary = "Check Product Deletion Eligibility",
            description = """
                    Checks whether a product can be safely deleted.

                    Returns:
                    • true  -> Product has no inventory.
                    • false -> Product still has stock or reserved quantity.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Deletion eligibility checked successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Can Delete",
                                            value = "true"
                                    ),
                                    @ExampleObject(
                                            name = "Cannot Delete",
                                            value = "false"
                                    )
                            }
                    )
            )

    })
    @GetMapping("/product/{productId}/can-delete")
    public Boolean canDeleteProduct(

            @Parameter(
                    description = "Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID productId) {

        return inventoryService.canDeleteProduct(productId);
    }

    @Operation(
            summary = "Delete Inventory",
            description = """
                    Deletes the inventory record of a product.

                    Business Rules:
                    • Inventory must exist.
                    • Inventory can only be deleted if both:
                        - Available quantity = 0
                        - Reserved quantity = 0
                    • Typically invoked internally by the Product Service
                      before deleting a product.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description = "Inventory deleted successfully"
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Inventory not found",
                                      "path":"/api/inventory/product/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Inventory cannot be deleted because stock is available",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":409,
                                      "error":"Conflict",
                                      "message":"Inventory cannot be deleted because stock is available",
                                      "path":"/api/inventory/product/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            )

    })
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteInventory(

            @Parameter(
                    description = "Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID productId) {

        inventoryService.deleteInventory(productId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reserve Inventory (Bulk)",
            description = """
                Reserves inventory for multiple products in a single request.

                Business Rules:
                • All products must have sufficient stock.
                • If any product has insufficient stock,
                  no inventory is reserved.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Inventory reserved successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Inventory not found"),
            @ApiResponse(responseCode = "409",
                    description = "Insufficient stock")
    })
    @PostMapping("/reserve-bulk")
    public  ResponseEntity<BulkReserveInventoryResponse>
    reserveBulkInventory(

            @Valid
            @RequestBody
            BulkReserveInventoryRequest request){

        return ResponseEntity.ok(

                inventoryService.reserveBulkInventory(request)

        );
    }

    @Operation(
            summary = "Release Inventory (Bulk)",
            description = """
                Releases reserved inventory for multiple products.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Inventory released successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Inventory not found")
    })
    @PostMapping("/release-bulk")
    public ResponseEntity<List<InventoryResponse>>
    releaseBulkInventory(

            @Valid
            @RequestBody
            BulkReleaseInventoryRequest request){

        return ResponseEntity.ok(

                inventoryService.releaseBulkInventory(request)

        );
    }

    @Operation(
            summary = "Confirm Inventory (Bulk)",
            description = """
                Confirms reserved inventory for multiple products.
                """
    )
    @ApiResponses(value={
            @ApiResponse(responseCode = "200",
                    description = "Inventory confirmed successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Inventory not found")
    })
    @PostMapping("/confirm-bulk")
    public ResponseEntity<List<InventoryResponse>>
    confirmBulkInventory(

            @Valid
            @RequestBody
            BulkConfirmInventoryRequest request){

        return ResponseEntity.ok(

                inventoryService.confirmBulkInventory(request)

        );
    }

}