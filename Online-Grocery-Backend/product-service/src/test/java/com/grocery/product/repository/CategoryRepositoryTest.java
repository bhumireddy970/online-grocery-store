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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@Rollback
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {

        category = new Category();

        category.setName("Fruits");

        category.setDescription("Fresh Fruits");

        category.setProducts(new ArrayList<>());

    }

    @Test
    @DisplayName("Should persist category when saving valid category")
    void givenValidCategoryWhenSaveThenPersistCategory() {

        Category savedCategory =
                categoryRepository.save(category);

        assertThat(savedCategory).isNotNull();

        assertThat(savedCategory.getId()).isNotNull();

        assertThat(savedCategory.getName())
                .isEqualTo("Fruits");

        assertThat(savedCategory.getDescription())
                .isEqualTo("Fresh Fruits");

    }

    @Test
    @DisplayName("Should return category when finding by existing id")
    void givenExistingCategoryIdWhenFindByIdThenReturnCategory() {

        Category savedCategory =
                categoryRepository.save(category);


        Optional<Category> result =
                categoryRepository.findById(savedCategory.getId());

        assertThat(result).isPresent();

        assertThat(result.get().getName())
                .isEqualTo("Fruits");

    }

    @Test
    @DisplayName("Should return empty optional when category id does not exist")
    void givenInvalidCategoryIdWhenFindByIdThenReturnEmptyOptional() {

        Optional<Category> result =
                categoryRepository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("Should return all categories when categories exist")
    void givenExistingCategoriesWhenFindAllThenReturnCategoryList() {

        categoryRepository.save(category);

        Category secondCategory = new Category();

        secondCategory.setName("Vegetables");

        secondCategory.setDescription("Fresh Vegetables");

        secondCategory.setProducts(new ArrayList<>());

        categoryRepository.save(secondCategory);

        List<Category> categories =
                categoryRepository.findAll();

        assertThat(categories)
                .hasSize(2);

    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void givenNoCategoriesWhenFindAllThenReturnEmptyList() {

        List<Category> categories =
                categoryRepository.findAll();

        assertThat(categories).isEmpty();

    }

    @Test
    @DisplayName("Should return true when category name exists")
    void givenExistingCategoryNameWhenExistsByNameThenReturnTrue() {

        categoryRepository.save(category);

        boolean result =
                categoryRepository.existsByName("Fruits");

        assertThat(result).isTrue();

    }

    @Test
    @DisplayName("Should return false when category name does not exist")
    void givenInvalidCategoryNameWhenExistsByNameThenReturnFalse() {

        boolean result =
                categoryRepository.existsByName("Vegetables");

        assertThat(result).isFalse();

    }

    @Test
    @DisplayName("Should return true when category id exists")
    void givenExistingCategoryIdWhenExistsByIdThenReturnTrue() {

        Category savedCategory =
                categoryRepository.save(category);

        boolean result =
                categoryRepository.existsById(savedCategory.getId());

        assertThat(result).isTrue();

    }

    @Test
    @DisplayName("Should return false when category id does not exist")
    void givenInvalidCategoryIdWhenExistsByIdThenReturnFalse() {

        boolean result =
                categoryRepository.existsById(UUID.randomUUID());

        assertThat(result).isFalse();

    }

    @Test
    @DisplayName("Should delete category when category exists")
    void givenExistingCategoryWhenDeleteThenRemoveCategory() {

        Category savedCategory =
                categoryRepository.save(category);

        categoryRepository.delete(savedCategory);

        Optional<Category> result =
                categoryRepository.findById(savedCategory.getId());

        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("Should keep remaining categories when deleting one category")
    void givenMultipleCategoriesWhenDeleteOneThenKeepRemainingCategories() {

        Category savedCategoryOne =
                categoryRepository.save(category);

        Category secondCategory = new Category();

        secondCategory.setName("Vegetables");

        secondCategory.setDescription("Fresh Vegetables");

        secondCategory.setProducts(new ArrayList<>());

        Category savedCategoryTwo =
                categoryRepository.save(secondCategory);

        categoryRepository.delete(savedCategoryOne);

        List<Category> categories =
                categoryRepository.findAll();

        assertThat(categories)
                .hasSize(1);

        assertThat(categories.get(0).getName())
                .isEqualTo("Vegetables");

    }

    @Test
    @DisplayName("Should persist category with associated products")
    void givenCategoryWithProductsWhenSaveThenPersistRelationship() {

        Product product = new Product();

        product.setSku("APL001");

        product.setName("Apple");

        product.setDescription("Fresh Apple");

        product.setPrice(BigDecimal.valueOf(180));

        product.setActive(true);

        product.setCategory(category);

        category.getProducts().add(product);

        Category savedCategory =
                categoryRepository.save(category);

        assertThat(savedCategory.getProducts())
                .hasSize(1);

        assertThat(savedCategory.getProducts()
                .get(0)
                .getName())
                .isEqualTo("Apple");

    }

    @Test
    @DisplayName("Should load associated products when finding category")
    void givenSavedCategoryWhenFindByIdThenReturnAssociatedProducts() {

        Product product = new Product();

        product.setSku("APL001");

        product.setName("Apple");

        product.setDescription("Fresh Apple");

        product.setPrice(BigDecimal.valueOf(180));

        product.setActive(true);

        product.setCategory(category);

        category.getProducts().add(product);

        Category savedCategory =
                categoryRepository.save(category);

        Category retrievedCategory =
                categoryRepository.findById(savedCategory.getId())
                        .orElseThrow();

        assertThat(retrievedCategory.getProducts())
                .hasSize(1);

        assertThat(retrievedCategory.getProducts()
                .get(0)
                .getSku())
                .isEqualTo("APL001");

    }
}