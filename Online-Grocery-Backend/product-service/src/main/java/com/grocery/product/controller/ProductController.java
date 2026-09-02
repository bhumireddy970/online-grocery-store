package com.grocery.product.controller;

import com.grocery.product.dto.BulkProductRequest;
import com.grocery.product.dto.CreateProductRequest;
import com.grocery.product.dto.ProductResponse;
import com.grocery.product.dto.UpdateProductRequest;
import com.grocery.product.exception.ErrorResponse;
import com.grocery.product.service.ProductService;
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
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")

@Tag(
        name = "Product Management API",
        description = """
                APIs for managing grocery store products.

                Features:
                • Create Product
                • View Products
                • Update Product
                • Delete Product
                • Verify Product Existence

                Products belong to categories and are used by
                Inventory Service and Order Service.
                """
)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Create Product",
            description = """
                    Creates a new grocery product.

                    Business Rules:
                    • SKU must be unique.
                    • Category must exist.
                    • Price must be greater than zero.
                    • Initial inventory will be created automatically.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "id":"550e8400-e29b-41d4-a716-446655440000",
                                      "sku":"APL001",
                                      "name":"Apple",
                                      "description":"Fresh Kashmiri Apples",
                                      "price":180.50,
                                      "active":true,
                                      "categoryId":"98d5d03b-12cb-46f3-a0d8-a5f3f8d9d287",
                                      "categoryName":"Fruits"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Price must be greater than zero",
                                      "path":"/api/products"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":404,
                                      "error":"Not Found",
                                      "message":"Category not found",
                                      "path":"/api/products"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "SKU already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":409,
                                      "error":"Conflict",
                                      "message":"SKU already exists",
                                      "path":"/api/products"
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
                                      "message":"Unable to create inventory because Inventory Service is unavailable",
                                      "path":"/api/products"
                                    }
                                    """
                            )
                    )
            )

    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "sku":"APL001",
                                      "name":"Apple",
                                      "description":"Fresh Kashmiri Apples",
                                      "price":180.50,
                                      "active":true,
                                      "categoryId":"98d5d03b-12cb-46f3-a0d8-a5f3f8d9d287",
                                      "initialQuantity":100
                                    }
                                    """
                            )
                    )
            )
            @Valid
            @RequestBody CreateProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get All Products",
            description = """
                    Retrieves all products available in the grocery store.

                    Returns product information including
                    category details.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    [
                                      {
                                        "id":"550e8400-e29b-41d4-a716-446655440000",
                                        "sku":"APL001",
                                        "name":"Apple",
                                        "description":"Fresh Kashmiri Apples",
                                        "price":180.50,
                                        "active":true,
                                        "categoryId":"98d5d03b-12cb-46f3-a0d8-a5f3f8d9d287",
                                        "categoryName":"Fruits"
                                      },
                                      {
                                        "id":"0dfeb04d-dbd7-49d2-b958-09c842d4e611",
                                        "sku":"MLK001",
                                        "name":"Milk",
                                        "description":"Fresh Cow Milk",
                                        "price":58.00,
                                        "active":true,
                                        "categoryId":"ab5c6372-8f90-4908-b090-9f10d4de89db",
                                        "categoryName":"Dairy"
                                      }
                                    ]
                                    """
                            )
                    )
            )

    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts());

    }
    @Operation(
            summary = "Get Product By ID",
            description = """
                    Retrieves complete details of a product using its unique identifier.

                    Returns product information along with its category details.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "id":"550e8400-e29b-41d4-a716-446655440000",
                                      "sku":"APL001",
                                      "name":"Apple",
                                      "description":"Fresh Kashmiri Apples",
                                      "price":180.50,
                                      "active":true,
                                      "categoryId":"98d5d03b-12cb-46f3-a0d8-a5f3f8d9d287",
                                      "categoryName":"Fruits"
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
                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            )

    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(

            @Parameter(
                    description = "Unique Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                productService.getProductById(id));

    }

    @Operation(
            summary = "Update Product",
            description = """
                    Updates an existing grocery product.

                    Business Rules:
                    • Product must exist.
                    • Category must exist.
                    • SKU must remain unique.
                    • Inventory quantity is not modified.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "id":"550e8400-e29b-41d4-a716-446655440000",
                                      "sku":"APL001",
                                      "name":"Organic Apple",
                                      "description":"Premium Organic Apples",
                                      "price":220.00,
                                      "active":true,
                                      "categoryId":"98d5d03b-12cb-46f3-a0d8-a5f3f8d9d287",
                                      "categoryName":"Fruits"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":400,
                                      "error":"Bad Request",
                                      "message":"Price must be greater than zero",
                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Product or Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Product Not Found",
                                            value = """
                                                    {
                                                      "timestamp":"2026-07-03T20:30:00",
                                                      "status":404,
                                                      "error":"Not Found",
                                                      "message":"Product not found",
                                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Category Not Found",
                                            value = """
                                                    {
                                                      "timestamp":"2026-07-03T20:30:00",
                                                      "status":404,
                                                      "error":"Not Found",
                                                      "message":"Category not found",
                                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "SKU already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp":"2026-07-03T20:30:00",
                                      "status":409,
                                      "error":"Conflict",
                                      "message":"SKU already exists",
                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            )

    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(

            @Parameter(
                    description = "Unique Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProductRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "sku":"APL001",
                                      "name":"Organic Apple",
                                      "description":"Premium Organic Apples",
                                      "price":220.00,
                                      "active":true,
                                      "categoryId":"98d5d03b-12cb-46f3-a0d8-a5f3f8d9d287"
                                    }
                                    """
                            )
                    )
            )
            @Valid
            @RequestBody UpdateProductRequest request) {

        ProductResponse response =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Check Product Exists",
            description = """
                    Checks whether a product exists.

                    This endpoint is intended for internal communication
                    between microservices such as Inventory Service and
                    Order Service.

                    Returns:
                    • true  -> Product exists.
                    • false -> Product does not exist.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Product existence verified successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Product Exists",
                                            value = "true"
                                    ),
                                    @ExampleObject(
                                            name = "Product Does Not Exist",
                                            value = "false"
                                    )
                            }
                    )
            )

    })
    @GetMapping("/{id}/exists")
    public Boolean existsProduct(

            @Parameter(
                    description = "Unique Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return productService.existsProduct(id);

    }

    @Operation(
            summary = "Delete Product",
            description = """
                    Deletes an existing product.

                    Business Rules:
                    • Product must exist.
                    • Product can only be deleted if no inventory exists.
                    • Inventory Service is consulted before deletion.
                    • Associated inventory is removed after successful validation.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    "Product deleted successfully."
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
                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Product cannot be deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Inventory Exists",
                                            value = """
                                                    {
                                                      "timestamp":"2026-07-03T20:30:00",
                                                      "status":409,
                                                      "error":"Conflict",
                                                      "message":"Product cannot be deleted because stock is available."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Product Referenced",
                                            value = """
                                                    {
                                                      "timestamp":"2026-07-03T20:30:00",
                                                      "status":409,
                                                      "error":"Conflict",
                                                      "message":"Product cannot be deleted because it is associated with existing orders."
                                                    }
                                                    """
                                    )
                            }
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
                                      "path":"/api/products/550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                            )
                    )
            )

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(

            @Parameter(
                    description = "Unique Product UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok("Product deleted successfully.");

    }

    @Operation(
            summary = "Get Products By IDs",
            description = """
                Retrieves multiple products in a single request.

                Business Rules:
                • Returns all products matching the supplied IDs.
                • Used internally by the Order Service to reduce
                  multiple Product Service calls into a single request.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                [
                                  {
                                    "id":"550e8400-e29b-41d4-a716-446655440000",
                                    "sku":"APL001",
                                    "name":"Apple",
                                    "description":"Fresh Kashmiri Apples",
                                    "price":180.50,
                                    "active":true,
                                    "categoryId":"550e8400-e29b-41d4-a716-446655440010",
                                    "categoryName":"Fruits"
                                  },
                                  {
                                    "id":"550e8400-e29b-41d4-a716-446655440001",
                                    "sku":"BAN001",
                                    "name":"Banana",
                                    "description":"Fresh Bananas",
                                    "price":60.00,
                                    "active":true,
                                    "categoryId":"550e8400-e29b-41d4-a716-446655440010",
                                    "categoryName":"Fruits"
                                  }
                                ]
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "timestamp":"2026-07-04T11:30:00",
                                  "status":400,
                                  "error":"Bad Request",
                                  "message":"Product ids are required",
                                  "path":"/api/products/bulk"
                                }
                                """
                            )
                    )
            )
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of Product IDs",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "productIds": [
                                    "550e8400-e29b-41d4-a716-446655440000",
                                    "550e8400-e29b-41d4-a716-446655440001",
                                    "550e8400-e29b-41d4-a716-446655440002"
                                  ]
                                }
                                """
                            )
                    )
            )
            @Valid
            @RequestBody BulkProductRequest request) {

        return ResponseEntity.ok(
                productService.getProductsByIds(request));
    }

}
