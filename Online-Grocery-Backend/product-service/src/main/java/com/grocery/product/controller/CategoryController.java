package com.grocery.product.controller;

import com.grocery.product.dto.CategoryRequest;
import com.grocery.product.dto.CategoryResponse;
import com.grocery.product.exception.ErrorResponse;
import com.grocery.product.service.CategoryService;
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
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(
        name = "Category Management API",
        description = """
                APIs for managing product categories in the Online Grocery Store.
                
                Features:
                • Create category
                • Retrieve all categories
                • Retrieve category by ID
                • Update category
                • Delete category
                
                Categories organize grocery products such as
                Fruits, Vegetables, Dairy, Bakery, Beverages,
                Snacks, Personal Care, Household Items, etc.
                """
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Create Category",
            description = """
                    Creates a new product category.
                    
                    Business Rules:
                    • Category name must be unique.
                    • Name is mandatory.
                    • Description is optional.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id":"550e8400-e29b-41d4-a716-446655440000",
                                              "name":"Fruits",
                                              "description":"Fresh seasonal fruits"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":400,
                                              "error":"Bad Request",
                                              "message":"Category name is required",
                                              "path":"/api/categories"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Category already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":409,
                                              "error":"Conflict",
                                              "message":"Category already exists",
                                              "path":"/api/categories"
                                            }
                                            """
                            )
                    )
            )

    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(

//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "Category details",
//                    required = true,
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = CategoryRequest.class),
//                            examples = @ExampleObject(
//                                    value = """
//                                            {
//                                              "name":"Fruits",
//                                              "description":"Fresh seasonal fruits"
//                                            }
//                                            """
//                            )
//                    )
//            )
            @Valid
            @RequestBody CategoryRequest request) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


    @Operation(
            summary = "Get All Categories",
            description = """
                    Retrieves all available product categories.
                    
                    Returns every category registered in the system.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "id":"550e8400-e29b-41d4-a716-446655440000",
                                                "name":"Fruits",
                                                "description":"Fresh seasonal fruits"
                                              },
                                              {
                                                "id":"8a41d2c1-4db4-49ea-94d8-0f3eb4b50787",
                                                "name":"Vegetables",
                                                "description":"Farm fresh vegetables"
                                              }
                                            ]
                                            """
                            )
                    )
            )

    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories());

    }

    @Operation(
            summary = "Get Category By ID",
            description = """
                    Retrieves the details of a category using its unique identifier.
                    
                    Returns category information including
                    category name and description.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Category retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id":"550e8400-e29b-41d4-a716-446655440000",
                                              "name":"Fruits",
                                              "description":"Fresh seasonal fruits"
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
                                              "path":"/api/categories/550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            )

    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(

            @Parameter(
                    description = "Unique Category UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(id));

    }

    @Operation(
            summary = "Update Category",
            description = """
                    Updates an existing product category.
                    
                    Business Rules:
                    • Category must exist.
                    • Category name must remain unique.
                    • Name cannot be blank.
                    • Description can be modified.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id":"550e8400-e29b-41d4-a716-446655440000",
                                              "name":"Organic Fruits",
                                              "description":"Fresh organic seasonal fruits"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":400,
                                              "error":"Bad Request",
                                              "message":"Category name is required",
                                              "path":"/api/categories/550e8400-e29b-41d4-a716-446655440000"
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
                                              "path":"/api/categories/550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Category name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":409,
                                              "error":"Conflict",
                                              "message":"Category already exists",
                                              "path":"/api/categories/550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            )

    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(

            @Parameter(
                    description = "Unique Category UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated category details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name":"Organic Fruits",
                                              "description":"Fresh organic seasonal fruits"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody CategoryRequest request) {

        CategoryResponse response =
                categoryService.updateCategory(id, request);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Delete Category",
            description = """
                    Deletes an existing product category.
                    
                    Business Rules:
                    • Category must exist.
                    • A category cannot be deleted if products are associated with it.
                    • Returns Conflict when the category is in use.
                    """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Category deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            "Category deleted successfully."
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
                                              "path":"/api/categories/550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Category contains products and cannot be deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp":"2026-07-03T20:30:00",
                                              "status":409,
                                              "error":"Conflict",
                                              "message":"Cannot delete category because it contains active products.",
                                              "path":"/api/categories/550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            )

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(

            @Parameter(
                    description = "Unique Category UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok("Category deleted successfully.");
    }


    public void test() {
        System.out.println("test");
    }
}