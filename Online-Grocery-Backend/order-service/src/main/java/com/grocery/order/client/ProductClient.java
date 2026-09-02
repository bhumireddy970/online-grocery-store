package com.grocery.order.client;

import com.grocery.order.dto.external.BulkProductRequest;
import com.grocery.order.dto.external.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "PRODUCT-SERVICE", url = "http://localhost:8080")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable UUID id);

    @PostMapping("/api/products/bulk")
    List<ProductResponse> getProducts(
            @RequestBody BulkProductRequest request);
}
