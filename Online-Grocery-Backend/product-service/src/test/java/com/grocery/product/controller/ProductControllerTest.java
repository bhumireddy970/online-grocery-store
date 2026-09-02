package com.grocery.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.product.dto.BulkProductRequest;
import com.grocery.product.dto.CreateProductRequest;
import com.grocery.product.dto.ProductResponse;
import com.grocery.product.dto.UpdateProductRequest;
import com.grocery.product.exception.ProductNotFoundException;
import com.grocery.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private UUID productId;

    private UUID categoryId;

    private CreateProductRequest createRequest;

    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

        productId = UUID.randomUUID();

        categoryId = UUID.randomUUID();

        createRequest = new CreateProductRequest(

                "APL001",

                "Apple",

                "Fresh Apple",

                BigDecimal.valueOf(180),

                true,

                categoryId,

                100

        );

        productResponse = new ProductResponse(

                productId,

                "APL001",

                "Apple",

                "Fresh Apple",

                BigDecimal.valueOf(180),

                true,

                categoryId,

                "Fruits",

                LocalDateTime.now(),

                LocalDateTime.now()

        );

    }

    @Test
    @DisplayName("Should create product when request is valid")
    void givenValidCreateProductRequestWhenCreateProductThenReturnCreatedStatus() throws Exception {

        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(

                        post("/api/products")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(createRequest))

                )

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id")
                        .value(productId.toString()))

                .andExpect(jsonPath("$.sku")
                        .value("APL001"))

                .andExpect(jsonPath("$.name")
                        .value("Apple"))

                .andExpect(jsonPath("$.categoryName")
                        .value("Fruits"));

        verify(productService)
                .createProduct(any(CreateProductRequest.class));

    }

    @Test
    @DisplayName("Should return bad request when create product request is invalid")
    void givenInvalidCreateProductRequestWhenCreateProductThenReturnBadRequest() throws Exception {

        CreateProductRequest request =

                new CreateProductRequest(

                        "",

                        "",

                        "",

                        BigDecimal.ZERO,

                        true,

                        null,

                        0

                );

        mockMvc.perform(

                        post("/api/products")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return all products when products exist")
    void givenExistingProductsWhenGetAllProductsThenReturnProductList() throws Exception {

        ProductResponse secondProduct =
                new ProductResponse(

                        UUID.randomUUID(),

                        "MLK001",

                        "Milk",

                        "Fresh Cow Milk",

                        BigDecimal.valueOf(60),

                        true,

                        UUID.randomUUID(),

                        "Dairy",

                        LocalDateTime.now(),

                        LocalDateTime.now()

                );

        when(productService.getAllProducts())
                .thenReturn(List.of(productResponse, secondProduct));

        mockMvc.perform(

                        get("/api/products")

                                .contentType(MediaType.APPLICATION_JSON)

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(2))

                .andExpect(jsonPath("$[0].sku")
                        .value("APL001"))

                .andExpect(jsonPath("$[0].name")
                        .value("Apple"))

                .andExpect(jsonPath("$[1].sku")
                        .value("MLK001"))

                .andExpect(jsonPath("$[1].name")
                        .value("Milk"));

        verify(productService)
                .getAllProducts();

    }

    @Test
    @DisplayName("Should return empty list when products do not exist")
    void givenNoProductsWhenGetAllProductsThenReturnEmptyList() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(List.of());

        mockMvc.perform(

                        get("/api/products")

                                .contentType(MediaType.APPLICATION_JSON)

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(0));

        verify(productService)
                .getAllProducts();

    }

    @Test
    @DisplayName("Should return product when product exists")
    void givenExistingProductIdWhenGetProductThenReturnProductResponse() throws Exception {

        when(productService.getProductById(productId))
                .thenReturn(productResponse);

        mockMvc.perform(

                        get("/api/products/{id}", productId)

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(productId.toString()))

                .andExpect(jsonPath("$.sku")
                        .value("APL001"))

                .andExpect(jsonPath("$.name")
                        .value("Apple"))

                .andExpect(jsonPath("$.description")
                        .value("Fresh Apple"))

                .andExpect(jsonPath("$.price")
                        .value(180))

                .andExpect(jsonPath("$.active")
                        .value(true))

                .andExpect(jsonPath("$.categoryId")
                        .value(categoryId.toString()))

                .andExpect(jsonPath("$.categoryName")
                        .value("Fruits"));

        verify(productService)
                .getProductById(productId);

    }

    @Test
    @DisplayName("Should return not found when product does not exist")
    void givenInvalidProductIdWhenGetProductThenReturnNotFound() throws Exception {

        UUID invalidProductId = UUID.randomUUID();

        when(productService.getProductById(invalidProductId))
                .thenThrow(

                        new ProductNotFoundException(

                                "Product not found"

                        )

                );

        mockMvc.perform(

                        get("/api/products/{id}", invalidProductId)

                )

                .andExpect(status().isNotFound());

        verify(productService)
                .getProductById(invalidProductId);

    }

    @Test
    @DisplayName("Should update product when request is valid")
    void givenValidUpdateProductRequestWhenUpdateProductThenReturnUpdatedProduct() throws Exception {

        UpdateProductRequest request =
                new UpdateProductRequest(

                        "APL001",

                        "Organic Apple",

                        "Premium Organic Apple",

                        BigDecimal.valueOf(220),

                        true,

                        categoryId

                );

        ProductResponse response =
                new ProductResponse(

                        productId,

                        "APL001",

                        "Organic Apple",

                        "Premium Organic Apple",

                        BigDecimal.valueOf(220),

                        true,

                        categoryId,

                        "Fruits",

                        LocalDateTime.now(),

                        LocalDateTime.now()

                );

        when(productService.updateProduct(productId, request))
                .thenReturn(response);

        mockMvc.perform(

                        put("/api/products/{id}", productId)

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.name")
                        .value("Organic Apple"))

                .andExpect(jsonPath("$.price")
                        .value(220));

        verify(productService)
                .updateProduct(productId, request);

    }

    @Test
    @DisplayName("Should return bad request when update request is invalid")
    void givenInvalidUpdateProductRequestWhenUpdateProductThenReturnBadRequest() throws Exception {

        UpdateProductRequest request =
                new UpdateProductRequest(

                        "",

                        "",

                        "",

                        BigDecimal.ZERO,

                        true,

                        null

                );

        mockMvc.perform(

                        put("/api/products/{id}", productId)

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should delete product when product exists")
    void givenExistingProductIdWhenDeleteProductThenReturnSuccessMessage() throws Exception {

        mockMvc.perform(

                        delete("/api/products/{id}", productId)

                )

                .andExpect(status().isOk())

                .andExpect(content().string("Product deleted successfully."));

        verify(productService)
                .deleteProduct(productId);

    }

    @Test
    @DisplayName("Should return not found when deleting non existing product")
    void givenInvalidProductIdWhenDeleteProductThenReturnNotFound() throws Exception {

        doThrow(new ProductNotFoundException("Product not found"))

                .when(productService)

                .deleteProduct(productId);

        mockMvc.perform(

                        delete("/api/products/{id}", productId)

                )

                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should return true when product exists")
    void givenExistingProductIdWhenExistsProductThenReturnTrue() throws Exception {

        when(productService.existsProduct(productId))
                .thenReturn(true);

        mockMvc.perform(

                        get("/api/products/{id}/exists", productId)

                )

                .andExpect(status().isOk())

                .andExpect(content().string("true"));

        verify(productService)
                .existsProduct(productId);

    }

    @Test
    @DisplayName("Should return false when product does not exist")
    void givenInvalidProductIdWhenExistsProductThenReturnFalse() throws Exception {

        UUID invalidProductId = UUID.randomUUID();

        when(productService.existsProduct(invalidProductId))
                .thenReturn(false);

        mockMvc.perform(

                        get("/api/products/{id}/exists", invalidProductId)

                )

                .andExpect(status().isOk())

                .andExpect(content().string("false"));

        verify(productService)
                .existsProduct(invalidProductId);

    }

    @Test
    @DisplayName("Should return products when valid product ids are provided")
    void givenValidProductIdsWhenGetProductsByIdsThenReturnProductList() throws Exception {

        BulkProductRequest request =
                new BulkProductRequest(

                        List.of(

                                productId,

                                UUID.randomUUID()

                        )

                );

        ProductResponse secondProduct =
                new ProductResponse(

                        UUID.randomUUID(),

                        "MLK001",

                        "Milk",

                        "Fresh Cow Milk",

                        BigDecimal.valueOf(60),

                        true,

                        UUID.randomUUID(),

                        "Dairy",

                        LocalDateTime.now(),

                        LocalDateTime.now()

                );

        when(productService.getProductsByIds(any(BulkProductRequest.class)))
                .thenReturn(List.of(productResponse, secondProduct));

        mockMvc.perform(

                        post("/api/products/bulk")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(2))

                .andExpect(jsonPath("$[0].name")
                        .value("Apple"))

                .andExpect(jsonPath("$[1].name")
                        .value("Milk"));

        verify(productService)
                .getProductsByIds(any(BulkProductRequest.class));

    }

    @Test
    @DisplayName("Should return empty list when no products are found")
    void givenNonExistingProductIdsWhenGetProductsByIdsThenReturnEmptyList() throws Exception {

        BulkProductRequest request =
                new BulkProductRequest(

                        List.of(

                                UUID.randomUUID(),

                                UUID.randomUUID()

                        )

                );

        when(productService.getProductsByIds(any(BulkProductRequest.class)))
                .thenReturn(List.of());

        mockMvc.perform(

                        post("/api/products/bulk")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(0));

        verify(productService)
                .getProductsByIds(any(BulkProductRequest.class));

    }


}
