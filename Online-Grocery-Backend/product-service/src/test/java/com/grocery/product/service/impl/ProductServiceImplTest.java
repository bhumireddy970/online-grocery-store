package com.grocery.product.service.impl;

import com.grocery.product.client.InventoryClient;
import com.grocery.product.client.dto.InventoryRequest;
import com.grocery.product.dto.BulkProductRequest;
import com.grocery.product.dto.CreateProductRequest;
import com.grocery.product.dto.ProductResponse;
import com.grocery.product.dto.UpdateProductRequest;
import com.grocery.product.entity.Category;
import com.grocery.product.entity.Product;
import com.grocery.product.exception.CategoryNotFoundException;
import com.grocery.product.exception.ProductNotFoundException;
import com.grocery.product.repository.CategoryRepository;
import com.grocery.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;

    private Product product;

    private UUID categoryId;

    private UUID productId;

    private CreateProductRequest request;


    @BeforeEach
    void setUp() {

        categoryId = UUID.randomUUID();

        productId = UUID.randomUUID();

        category = new Category();

        category.setId(categoryId);

        category.setName("Fruits");

        product = new Product();

        product.setId(productId);

        product.setSku("SKU100");

        product.setName("Apple");

        product.setDescription("Fresh Apple");

        product.setPrice(BigDecimal.valueOf(120));

        product.setActive(true);

        product.setCategory(category);

        request = new CreateProductRequest(

                "SKU100",

                "Apple",

                "Fresh Apple",

                BigDecimal.valueOf(120),

                true,

                categoryId,

                100

        );

    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {

        when(productRepository.existsBySku(request.sku()))
                .thenReturn(false);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.createProduct(request);

        assertThat(response).isNotNull();

        assertThat(response.name())
                .isEqualTo("Apple");

        assertThat(response.categoryName())
                .isEqualTo("Fruits");

        verify(productRepository)
                .save(any(Product.class));

        verify(inventoryClient)
                .addInventory(any(InventoryRequest.class));

    }

    @Test
    @DisplayName("Should return all products when products exist")
    void givenProductsExist_whenGetAllProducts_thenReturnProductList() {

        // Arrange

        Product product2 = new Product();

        product2.setId(UUID.randomUUID());

        product2.setSku("SKU200");

        product2.setName("Mango");

        product2.setDescription("Fresh Mango");

        product2.setPrice(BigDecimal.valueOf(150));

        product2.setActive(true);

        product2.setCategory(category);

        when(productRepository.findAll())
                .thenReturn(List.of(product, product2));

        // Act

        List<ProductResponse> response =
                productService.getAllProducts();

        // Assert

        assertThat(response)
                .hasSize(2);

        assertThat(response)
                .extracting(ProductResponse::name)
                .containsExactly(
                        "Apple",
                        "Mango");

        verify(productRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should return an empty list when no products exist")
    void givenNoProductsExist_whenGetAllProducts_thenReturnEmptyList() {



        when(productRepository.findAll())
                .thenReturn(List.of());



        List<ProductResponse> response =
                productService.getAllProducts();

        assertThat(response)
                .isEmpty();

        verify(productRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should return product when product ID exists")
    void givenExistingProductId_whenGetProductById_thenReturnProduct() {

        // Arrange

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        // Act

        ProductResponse response =
                productService.getProductById(productId);

        // Assert

        assertThat(response)
                .isNotNull();

        assertThat(response.id())
                .isEqualTo(productId);

        assertThat(response.name())
                .isEqualTo("Apple");

        verify(productRepository)
                .findById(productId);

    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void givenInvalidProductId_whenGetProductById_thenThrowProductNotFoundException() {


        UUID invalidProductId = UUID.randomUUID();

        when(productRepository.findById(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productService.getProductById(invalidProductId))

                .isInstanceOf(ProductNotFoundException.class)

                .hasMessage("Product not found");

        verify(productRepository)
                .findById(invalidProductId);

    }

    @Test
    @DisplayName("Should update product when product and category exist")
    void givenValidUpdateRequest_whenUpdateProduct_thenReturnUpdatedProduct() {

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "SKU200",
                "Green Apple",
                "Fresh Green Apple",
                BigDecimal.valueOf(180),
                true,
                categoryId
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response =
                productService.updateProduct(productId, updateRequest);

        assertThat(response).isNotNull();

        assertThat(response.sku())
                .isEqualTo("SKU200");

        assertThat(response.name())
                .isEqualTo("Green Apple");

        assertThat(response.description())
                .isEqualTo("Fresh Green Apple");

        assertThat(response.price())
                .isEqualByComparingTo(BigDecimal.valueOf(180));

        verify(productRepository)
                .findById(productId);

        verify(categoryRepository)
                .findById(categoryId);

        verify(productRepository)
                .save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existing product")
    void givenInvalidProductId_whenUpdateProduct_thenThrowProductNotFoundException() {

        UUID invalidProductId = UUID.randomUUID();

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "SKU200",
                "Apple",
                "Fresh Apple",
                BigDecimal.valueOf(120),
                true,
                categoryId
        );

        when(productRepository.findById(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productService.updateProduct(invalidProductId, updateRequest))

                .isInstanceOf(ProductNotFoundException.class)

                .hasMessage("Product not found");

        verify(productRepository)
                .findById(invalidProductId);

        verify(categoryRepository, never())
                .findById(any());

        verify(productRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when updating with invalid category")
    void givenInvalidCategoryId_whenUpdateProduct_thenThrowCategoryNotFoundException() {

        UUID invalidCategoryId = UUID.randomUUID();

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "SKU200",
                "Apple",
                "Fresh Apple",
                BigDecimal.valueOf(120),
                true,
                invalidCategoryId
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(invalidCategoryId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productService.updateProduct(productId, updateRequest))

                .isInstanceOf(CategoryNotFoundException.class)

                .hasMessage("Category not found");

        verify(productRepository)
                .findById(productId);

        verify(categoryRepository)
                .findById(invalidCategoryId);

        verify(productRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Should delete product when inventory can be deleted")
    void givenExistingProductIdWhenDeleteProductThenDeleteProduct() {

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(inventoryClient.canDeleteProduct(productId))
                .thenReturn(true);

        productService.deleteProduct(productId);

        verify(productRepository)
                .findById(productId);

        verify(inventoryClient)
                .canDeleteProduct(productId);

        verify(inventoryClient)
                .deleteInventory(productId);

        verify(productRepository)
                .delete(product);
    }

    @Test
    @DisplayName("Should return true when product exists")
    void givenExistingProductIdWhenExistsProductThenReturnTrue() {

        when(productRepository.existsById(productId))
                .thenReturn(true);

        Boolean result =
                productService.existsProduct(productId);

        assertThat(result).isTrue();

        verify(productRepository)
                .existsById(productId);

    }

    @Test
    @DisplayName("Should return false when product does not exist")
    void givenInvalidProductIdWhenExistsProductThenReturnFalse() {

        UUID invalidProductId = UUID.randomUUID();

        when(productRepository.existsById(invalidProductId))
                .thenReturn(false);

        Boolean result =
                productService.existsProduct(invalidProductId);

        assertThat(result).isFalse();

        verify(productRepository)
                .existsById(invalidProductId);

    }

    @Test
    @DisplayName("Should return products when product IDs exist")
    void givenExistingProductIdsWhenGetProductsByIdsThenReturnProducts() {

        Product secondProduct = new Product();

        secondProduct.setId(UUID.randomUUID());
        secondProduct.setSku("SKU200");
        secondProduct.setName("Banana");
        secondProduct.setDescription("Fresh Banana");
        secondProduct.setPrice(BigDecimal.valueOf(60));
        secondProduct.setActive(true);
        secondProduct.setCategory(category);

        BulkProductRequest request =
                new BulkProductRequest(
                        List.of(productId, secondProduct.getId()));

        when(productRepository.findAllById(request.productIds()))
                .thenReturn(List.of(product, secondProduct));

        List<ProductResponse> response =
                productService.getProductsByIds(request);

        assertThat(response)
                .hasSize(2);

        assertThat(response)
                .extracting(ProductResponse::name)
                .containsExactly(
                        "Apple",
                        "Banana");

        verify(productRepository)
                .findAllById(request.productIds());

    }

    @Test
    @DisplayName("Should return empty list when product IDs do not exist")
    void givenNonExistingProductIdsWhenGetProductsByIdsThenReturnEmptyList() {

        BulkProductRequest request =
                new BulkProductRequest(
                        List.of(UUID.randomUUID(), UUID.randomUUID()));

        when(productRepository.findAllById(request.productIds()))
                .thenReturn(List.of());

        List<ProductResponse> response =
                productService.getProductsByIds(request);

        assertThat(response).isEmpty();

        verify(productRepository)
                .findAllById(request.productIds());

    }

}