package com.grocery.order.service.communication;

import com.grocery.order.client.ProductClient;
import com.grocery.order.dto.external.BulkProductRequest;
import com.grocery.order.dto.external.ProductResponse;
import com.grocery.order.exception.ProductServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductCommunicationService {

    private final ProductClient productClient;

    public ProductCommunicationService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "productFallback")
    @Retry(name = "productService")
    public ProductResponse getProduct(UUID productId) {

        return productClient.getProductById(productId);

    }

    public ProductResponse productFallback(
            Long productId,
            Exception ex) {

        System.out.println("Circuit Breaker Fallback Executed");

        throw new ProductServiceException(
                "Product Service is unavailable");

    }

    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "productsFallback")
    @Retry(name = "productService")
    public List<ProductResponse> getProducts(
            BulkProductRequest request){

        return productClient.getProducts(request);

    }
    public List<ProductResponse> productsFallback(
            BulkProductRequest request,
            Exception ex){

        throw new ProductServiceException(
                "Unable to fetch products.");

    }

}