package com.grocery.product.repository;

import com.grocery.product.entity.Category;
import com.grocery.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
@Rollback
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    private Product product;

    @BeforeEach
    void setUp() {

        category = new Category();

        category.setName("Fruits");

        category.setDescription("Fresh Fruits");

        category = categoryRepository.save(category);

        product = new Product();

        product.setSku("APL001");

        product.setName("Apple");

        product.setDescription("Fresh Apple");

        product.setPrice(BigDecimal.valueOf(180));

        product.setActive(true);

        product.setCategory(category);

    }

    @Test
    @DisplayName("Should persist product when saving valid product")
    void givenValidProductWhenSaveThenPersistProduct() {

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();

        assertThat(savedProduct.getId()).isNotNull();

        assertThat(savedProduct.getSku())
                .isEqualTo("APL001");

        assertThat(savedProduct.getCategory().getName())
                .isEqualTo("Fruits");

    }

    @Test
    @DisplayName("Should return product when finding by existing id")
    void givenExistingProductIdWhenFindByIdThenReturnProduct() {

        Product savedProduct = productRepository.save(product);

        Optional<Product> result =
                productRepository.findById(savedProduct.getId());

        assertThat(result).isPresent();

        assertThat(result.get().getSku())
                .isEqualTo("APL001");

        assertThat(result.get().getName())
                .isEqualTo("Apple");

    }

    @Test
    @DisplayName("Should return empty optional when product id does not exist")
    void givenInvalidProductIdWhenFindByIdThenReturnEmptyOptional() {

        Optional<Product> result =
                productRepository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("Should return all products when products exist")
    void givenExistingProductsWhenFindAllThenReturnProductList() {

        productRepository.save(product);

        Product secondProduct = new Product();

        secondProduct.setSku("MLK001");

        secondProduct.setName("Milk");

        secondProduct.setDescription("Fresh Milk");

        secondProduct.setPrice(BigDecimal.valueOf(60));

        secondProduct.setActive(true);

        secondProduct.setCategory(category);

        productRepository.save(secondProduct);

        List<Product> products =
                productRepository.findAll();

        assertThat(products)
                .hasSize(2);

    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void givenNoProductsWhenFindAllThenReturnEmptyList() {

        List<Product> products =
                productRepository.findAll();

        assertThat(products).isEmpty();

    }

    @Test
    @DisplayName("Should return product when SKU exists")
    void givenExistingSkuWhenFindBySkuThenReturnProduct() {

        productRepository.save(product);

        Optional<Product> result =
                productRepository.findBySku("APL001");

        assertThat(result).isPresent();

        assertThat(result.get().getSku())
                .isEqualTo("APL001");

        assertThat(result.get().getName())
                .isEqualTo("Apple");

    }

    @Test
    @DisplayName("Should return empty optional when SKU does not exist")
    void givenInvalidSkuWhenFindBySkuThenReturnEmptyOptional() {

        Optional<Product> result =
                productRepository.findBySku("UNKNOWN");

        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("Should return true when SKU exists")
    void givenExistingSkuWhenExistsBySkuThenReturnTrue() {

        productRepository.save(product);

        boolean result =
                productRepository.existsBySku("APL001");

        assertThat(result).isTrue();

    }

    @Test
    @DisplayName("Should return false when SKU does not exist")
    void givenInvalidSkuWhenExistsBySkuThenReturnFalse() {

        boolean result =
                productRepository.existsBySku("UNKNOWN");

        assertThat(result).isFalse();

    }

    @Test
    @DisplayName("Should return true when product id exists")
    void givenExistingProductIdWhenExistsByIdThenReturnTrue() {

        Product savedProduct =
                productRepository.save(product);

        boolean result =
                productRepository.existsById(savedProduct.getId());

        assertThat(result).isTrue();

    }

    @Test
    @DisplayName("Should return false when product id does not exist")
    void givenInvalidProductIdWhenExistsByIdThenReturnFalse() {

        boolean result =
                productRepository.existsById(UUID.randomUUID());

        assertThat(result).isFalse();

    }

    @Test
    @DisplayName("Should return matching products when finding by multiple ids")
    void givenExistingProductIdsWhenFindAllByIdThenReturnMatchingProducts() {

        Product savedProductOne =
                productRepository.save(product);

        Product secondProduct = new Product();

        secondProduct.setSku("MLK001");

        secondProduct.setName("Milk");

        secondProduct.setDescription("Fresh Milk");

        secondProduct.setPrice(BigDecimal.valueOf(60));

        secondProduct.setActive(true);

        secondProduct.setCategory(category);

        Product savedProductTwo =
                productRepository.save(secondProduct);

        List<Product> products =
                productRepository.findAllById(

                        List.of(

                                savedProductOne.getId(),

                                savedProductTwo.getId()

                        )

                );

        assertThat(products)
                .hasSize(2);

        assertThat(products)
                .extracting(Product::getSku)
                .containsExactlyInAnyOrder(

                        "APL001",

                        "MLK001"

                );

    }

    @Test
    @DisplayName("Should return available products when some product ids exist")
    void givenPartialExistingProductIdsWhenFindAllByIdThenReturnAvailableProducts() {

        Product savedProduct =
                productRepository.save(product);

        List<Product> products =
                productRepository.findAllById(

                        List.of(

                                savedProduct.getId(),

                                UUID.randomUUID()

                        )

                );

        assertThat(products)
                .hasSize(1);

        assertThat(products.get(0).getSku())
                .isEqualTo("APL001");

    }

    @Test
    @DisplayName("Should return empty list when product ids do not exist")
    void givenInvalidProductIdsWhenFindAllByIdThenReturnEmptyList() {

        List<Product> products =
                productRepository.findAllById(

                        List.of(

                                UUID.randomUUID(),

                                UUID.randomUUID()

                        )

                );

        assertThat(products).isEmpty();

    }

    @Test
    @DisplayName("Should delete product when product exists")
    void givenExistingProductWhenDeleteThenRemoveProduct() {

        Product savedProduct =
                productRepository.save(product);

        productRepository.delete(savedProduct);

        Optional<Product> result =
                productRepository.findById(savedProduct.getId());

        assertThat(result).isEmpty();

    }

    

}