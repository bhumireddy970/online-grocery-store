package com.grocery.order.controller;

import com.grocery.order.dto.CreateOrderRequest;
import com.grocery.order.dto.OrderResponse;
import com.grocery.order.exception.ErrorResponse;
import com.grocery.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(
        name = "Order Management API",
        description = """
                APIs for managing customer orders in the Online Grocery Store.

                Features:
                • Create a new order
                • View all orders
                • View order details
                • Confirm an order
                • Cancel an order
                • Deliver an order

                During order creation, the service validates the customer,
                verifies product availability, reserves inventory,
                and calculates the total order amount.
                """
)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Create Order",
            description = """
                    Creates a new customer order.

                    Business Process:
                    • Validates customer.
                    • Validates product availability.
                    • Reserves inventory.
                    • Calculates total amount.
                    • Creates order with PENDING status.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "orderId":"550e8400-e29b-41d4-a716-446655440000",
                                      "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                      "status":"PENDING",
                                      "orderDate":"2026-07-03T20:30:00",
                                      "totalAmount":470.00,
                                      "items":[
                                        {
                                          "productId":"d3ec4f5d-5d26-47b4-a0ef-f5f58a6e0b42",
                                          "productName":"Apple",
                                          "price":180.00,
                                          "quantity":2,
                                          "subTotal":360.00
                                        },
                                        {
                                          "productId":"4d65417d-4b95-4dcb-a58c-5b4d66d317e7",
                                          "productName":"Milk",
                                          "price":110.00,
                                          "quantity":1,
                                          "subTotal":110.00
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Order must contain at least one item",
                                      "path":"/api/orders"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {

                                    @ExampleObject(
                                            name = "Customer Not Found",
                                            value = """
                                                    {
                                                      "timestamp":"2026-07-03T20:30:00",
                                                      "status":404,
                                                      "error":"Not Found",
                                                      "message":"Customer not found",
                                                      "path":"/api/orders"
                                                    }
                                                    """
                                    ),

                                    @ExampleObject(
                                            name = "Product Not Found",
                                            value = """
                                                    {
                                                      "timestamp":"2026-07-03T20:30:00",
                                                      "status":404,
                                                      "error":"Not Found",
                                                      "message":"Product not found",
                                                      "path":"/api/orders"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Insufficient inventory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":409,
                                      "error":"Conflict",
                                      "message":"Insufficient stock available",
                                      "path":"/api/orders"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "503",
                    description = "Dependent service unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":503,
                                      "error":"Service Unavailable",
                                      "message":"Inventory Service is unavailable",
                                      "path":"/api/orders"
                                    }
                                    """
                            )
                    )
            )

    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer order information",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                      "items":[
                                        {
                                          "productId":"d3ec4f5d-5d26-47b4-a0ef-f5f58a6e0b42",
                                          "quantity":2
                                        },
                                        {
                                          "productId":"4d65417d-4b95-4dcb-a58c-5b4d66d317e7",
                                          "quantity":1
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            )
            @Valid
            @RequestBody CreateOrderRequest request) {

        OrderResponse createdOrder =
                orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    @Operation(
            summary = "Get All Orders",
            description = "Retrieves all customer orders."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    [
                                      {
                                        "orderId":"550e8400-e29b-41d4-a716-446655440000",
                                        "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                        "status":"PENDING",
                                        "orderDate":"2026-07-03T20:30:00",
                                        "totalAmount":470.00
                                      },
                                      {
                                        "orderId":"e96373fd-f65c-4cb4-b66f-5a2d48777a40",
                                        "customerId":"5fb8cc73-0c91-4a4c-9db0-fde00d6bfa9d",
                                        "status":"DELIVERED",
                                        "orderDate":"2026-07-02T18:20:00",
                                        "totalAmount":820.00
                                      }
                                    ]
                                    """
                            )
                    )
            )

    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders());

    }

    @GetMapping("/customer/{customerid}")
    public ResponseEntity<List<OrderResponse>> getOrdersbycustomerid(@PathVariable UUID customerid) {

        return ResponseEntity.ok(
                orderService.getOrderByCustomerId(customerid));

    }

    @Operation(
            summary = "Get Order By ID",
            description = """
                    Retrieves complete details of an order including
                    customer information, ordered products,
                    quantities and total amount.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "orderId":"550e8400-e29b-41d4-a716-446655440000",
                                      "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                      "status":"PENDING",
                                      "orderDate":"2026-07-03T20:30:00",
                                      "totalAmount":470.00,
                                      "items":[
                                        {
                                          "productId":"d3ec4f5d-5d26-47b4-a0ef-f5f58a6e0b42",
                                          "productName":"Apple",
                                          "price":180.00,
                                          "quantity":2,
                                          "subTotal":360.00
                                        },
                                        {
                                          "productId":"4d65417d-4b95-4dcb-a58c-5b4d66d317e7",
                                          "productName":"Milk",
                                          "price":110.00,
                                          "quantity":1,
                                          "subTotal":110.00
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Order not found",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            )

    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderbyid(

            @Parameter(
                    description = "Unique Order UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                orderService.getOrder(id));

    }

    @Operation(
            summary = "Confirm Order",
            description = """
                    Confirms an existing order.

                    Business Rules:
                    • Order must be in PENDING status.
                    • Reserved inventory becomes permanently deducted.
                    • Order status changes to CONFIRMED.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Order confirmed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "orderId":"550e8400-e29b-41d4-a716-446655440000",
                                      "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                      "status":"CONFIRMED",
                                      "orderDate":"2026-07-03T20:30:00",
                                      "totalAmount":470.00
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Only pending orders can be confirmed",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/confirm"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Order not found",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/confirm"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "503",
                    description = "Inventory Service unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":503,
                                      "error":"Service Unavailable",
                                      "message":"Inventory Service is unavailable",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/confirm"
                                    }
                                    """
                            )
                    )
            )

    })
    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(

            @Parameter(
                    description = "Unique Order UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                orderService.confirmOrder(id));

    }

    @Operation(
            summary = "Cancel Order",
            description = """
                    Cancels an existing order.

                    Business Rules:
                    • Only PENDING orders can be cancelled.
                    • Reserved inventory is released.
                    • Order status changes to CANCELLED.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "orderId":"550e8400-e29b-41d4-a716-446655440000",
                                      "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                      "status":"CANCELLED",
                                      "orderDate":"2026-07-03T20:30:00",
                                      "totalAmount":470.00
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Order cannot be cancelled",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Only pending orders can be cancelled",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/cancel"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Order not found",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/cancel"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "503",
                    description = "Inventory Service unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":503,
                                      "error":"Service Unavailable",
                                      "message":"Inventory Service is unavailable",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/cancel"
                                    }
                                    """
                            )
                    )
            )

    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(

            @Parameter(
                    description = "Unique Order UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                orderService.cancelOrder(id));

    }

    @Operation(
            summary = "Deliver Order",
            description = """
                    Marks an order as delivered.

                    Business Rules:
                    • Only CONFIRMED orders can be delivered.
                    • Order status changes to DELIVERED.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Order delivered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "orderId":"550e8400-e29b-41d4-a716-446655440000",
                                      "customerId":"7dd87dc3-c6a3-49fc-aaf3-8f891c74c7b9",
                                      "status":"DELIVERED",
                                      "orderDate":"2026-07-03T20:30:00",
                                      "totalAmount":470.00
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Order is not eligible for delivery",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Only confirmed orders can be delivered",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/deliver"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Order not found",
                                      "path":"/api/orders/550e8400-e29b-41d4-a716-446655440000/deliver"
                                    }
                                    """
                            )
                    )
            )

    })
    @PutMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(

            @Parameter(
                    description = "Unique Order UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                orderService.deliverOrder(id));

    }

}

