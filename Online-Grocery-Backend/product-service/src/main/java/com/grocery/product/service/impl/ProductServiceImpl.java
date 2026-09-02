    package com.grocery.product.service.impl;

    import com.grocery.product.client.InventoryClient;
    import com.grocery.product.client.dto.InventoryRequest;
    import com.grocery.product.dto.BulkProductRequest;
    import com.grocery.product.dto.CreateProductRequest;
    import com.grocery.product.dto.ProductResponse;
    import com.grocery.product.dto.UpdateProductRequest;
    import com.grocery.product.entity.Category;
    import com.grocery.product.entity.Product;
    import com.grocery.product.exception.*;
    import com.grocery.product.repository.CategoryRepository;
    import com.grocery.product.repository.ProductRepository;
    import com.grocery.product.service.ProductService;
    import feign.FeignException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class ProductServiceImpl implements ProductService {

        private final InventoryClient inventoryClient;
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;


        @Override
        public ProductResponse createProduct(CreateProductRequest request) {

            if (productRepository.existsBySku(request.sku())) {
                throw new DuplicateSkuException("SKU already exists");
            }

            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() ->
                            new CategoryNotFoundException("Category not found"));

            Product product = new Product();

            product.setSku(request.sku());
            product.setName(request.name());
            product.setDescription(request.description());
            product.setPrice(request.price());
            product.setActive(request.active());
            product.setCategory(category);

            Product savedProduct = productRepository.save(product);

            try {

                InventoryRequest inventory =
                        new InventoryRequest(
                                savedProduct.getId(),
                                request.initialQuantity()
                        );

                inventoryClient.addInventory(inventory);

            }
            catch (FeignException ex) {

                productRepository.delete(savedProduct);

                throw new InventoryServiceUnavailableException(
                        "Unable to create inventory because Inventory Service is unavailable.",
                        ex
                );

            }

            return mapToResponse(savedProduct);

        }

        @Override
        public List<ProductResponse> getAllProducts() {

            return productRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

        }

        @Override
        public ProductResponse getProductById(UUID id) {

            Product product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new ProductNotFoundException("Product not found"));

            return mapToResponse(product);

        }

        @Override
        public ProductResponse updateProduct(UUID id,
                                             UpdateProductRequest request) {

            Product product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new ProductNotFoundException("Product not found"));

            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() ->
                            new CategoryNotFoundException("Category not found"));

            product.setSku(request.sku());
            product.setName(request.name());
            product.setDescription(request.description());
            product.setPrice(request.price());
            product.setActive(request.active());
            product.setCategory(category);

            Product updatedProduct = productRepository.save(product);


            return mapToResponse(updatedProduct);

        }

        @Override
        public void deleteProduct(UUID id) {

            Product product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new ProductNotFoundException("Product not found"));

            Boolean canDelete =
                    inventoryClient.canDeleteProduct(id);

            if (Boolean.FALSE.equals(canDelete)) {

                throw new ProductDeletionException(
                        "Product cannot be deleted because stock is available.");

            }

            inventoryClient.deleteInventory(id);

            productRepository.delete(product);

        }

        @Override
        public Boolean existsProduct(UUID id) {
            return productRepository.existsById(id);
        }

        private ProductResponse mapToResponse(Product product) {

            return new ProductResponse(

                    product.getId(),

                    product.getSku(),

                    product.getName(),

                    product.getDescription(),

                    product.getPrice(),

                    product.getActive(),

                    product.getCategory().getId(),

                    product.getCategory().getName(),

                    product.getCreatedAt(),

                    product.getUpdatedAt()

            );

        }

        @Override
        public List<ProductResponse> getProductsByIds(
                BulkProductRequest request) {

            List<Product> products =
                    productRepository.findAllById(
                            request.productIds());

            return products.stream()
                    .map(this::mapToResponse)
                    .toList();

        }

    }
