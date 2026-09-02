package com.grocery.product.service;

import com.grocery.product.dto.BulkProductRequest;
import com.grocery.product.dto.CreateProductRequest;
import com.grocery.product.dto.ProductResponse;
import com.grocery.product.dto.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(UUID id);

    ProductResponse updateProduct(UUID id, UpdateProductRequest request);

    void deleteProduct(UUID id);
    Boolean existsProduct(UUID id);

    List<ProductResponse> getProductsByIds(
            BulkProductRequest request);
}
