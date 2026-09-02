package com.grocery.product.service.impl;

import com.grocery.product.dto.CategoryRequest;
import com.grocery.product.dto.CategoryResponse;
import com.grocery.product.entity.Category;
import com.grocery.product.entity.Product;
import com.grocery.product.exception.CategoryInUseException;
import com.grocery.product.exception.CategoryNotFoundException;
import com.grocery.product.exception.DuplicateCategoryException;
import com.grocery.product.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;

    private Category category;

    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {

        categoryId = UUID.randomUUID();

        category = new Category();

        category.setId(categoryId);

        category.setName("Fruits");

        category.setDescription("Fresh Fruits");

        category.setProducts(new ArrayList<>());

        categoryRequest = new CategoryRequest(

                "Fruits",

                "Fresh Fruits"

        );

    }

    @Test
    @DisplayName("Should create category when category name does not exist")
    void givenUniqueCategoryNameWhenCreateCategoryThenReturnCategoryResponse() {

        when(categoryRepository.existsByName(categoryRequest.name()))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponse response =
                categoryService.createCategory(categoryRequest);

        assertThat(response).isNotNull();

        assertThat(response.id())
                .isEqualTo(categoryId);

        assertThat(response.name())
                .isEqualTo("Fruits");

        assertThat(response.description())
                .isEqualTo("Fresh Fruits");

        verify(categoryRepository)
                .existsByName(categoryRequest.name());

        verify(categoryRepository)
                .save(any(Category.class));

    }

    @Test
    @DisplayName("Should throw DuplicateCategoryException when category name already exists")
    void givenExistingCategoryNameWhenCreateCategoryThenThrowDuplicateCategoryException() {

        when(categoryRepository.existsByName(categoryRequest.name()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                categoryService.createCategory(categoryRequest))

                .isInstanceOf(DuplicateCategoryException.class)

                .hasMessage("Category already exists");

        verify(categoryRepository)
                .existsByName(categoryRequest.name());

        verify(categoryRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should return all categories when categories exist")
    void givenExistingCategoriesWhenGetAllCategoriesThenReturnCategoryList() {

        Category secondCategory = new Category();

        secondCategory.setId(UUID.randomUUID());

        secondCategory.setName("Vegetables");

        secondCategory.setDescription("Fresh Vegetables");

        secondCategory.setProducts(new ArrayList<>());

        when(categoryRepository.findAll())
                .thenReturn(List.of(category, secondCategory));

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        assertThat(response)
                .hasSize(2);

        assertThat(response)
                .extracting(CategoryResponse::name)
                .containsExactly(
                        "Fruits",
                        "Vegetables");

        verify(categoryRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void givenNoCategoriesWhenGetAllCategoriesThenReturnEmptyList() {

        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        assertThat(response).isEmpty();

        verify(categoryRepository)
                .findAll();

    }

    @Test
    @DisplayName("Should return category when category exists")
    void givenExistingCategoryIdWhenGetCategoryByIdThenReturnCategoryResponse() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        CategoryResponse response =
                categoryService.getCategoryById(categoryId);

        assertThat(response).isNotNull();

        assertThat(response.id())
                .isEqualTo(categoryId);

        assertThat(response.name())
                .isEqualTo("Fruits");

        assertThat(response.description())
                .isEqualTo("Fresh Fruits");

        verify(categoryRepository)
                .findById(categoryId);

    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category does not exist")
    void givenInvalidCategoryIdWhenGetCategoryByIdThenThrowCategoryNotFoundException() {

        UUID invalidCategoryId = UUID.randomUUID();

        when(categoryRepository.findById(invalidCategoryId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.getCategoryById(invalidCategoryId))

                .isInstanceOf(CategoryNotFoundException.class)

                .hasMessage("Category not found");

        verify(categoryRepository)
                .findById(invalidCategoryId);

    }

    @Test
    @DisplayName("Should update category when category exists")
    void givenExistingCategoryWhenUpdateCategoryThenReturnUpdatedCategoryResponse() {

        CategoryRequest updateRequest =
                new CategoryRequest(

                        "Vegetables",

                        "Fresh Vegetables"

                );

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response =
                categoryService.updateCategory(
                        categoryId,
                        updateRequest);

        assertThat(response).isNotNull();

        assertThat(response.name())
                .isEqualTo("Vegetables");

        assertThat(response.description())
                .isEqualTo("Fresh Vegetables");

        verify(categoryRepository)
                .findById(categoryId);

        verify(categoryRepository)
                .save(any(Category.class));

    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when updating non existing category")
    void givenInvalidCategoryIdWhenUpdateCategoryThenThrowCategoryNotFoundException() {

        UUID invalidCategoryId = UUID.randomUUID();

        when(categoryRepository.findById(invalidCategoryId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.updateCategory(
                        invalidCategoryId,
                        categoryRequest))

                .isInstanceOf(CategoryNotFoundException.class)

                .hasMessage("Category not found");

        verify(categoryRepository)
                .findById(invalidCategoryId);

        verify(categoryRepository, never())
                .save(any());

    }

    @Test
    @DisplayName("Should delete category when category does not contain any products")
    void givenExistingUnusedCategoryWhenDeleteCategoryThenDeleteCategory() {

        category.setProducts(new ArrayList<>());

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository)
                .findById(categoryId);

        verify(categoryRepository)
                .delete(category);

    }

    @Test
    @DisplayName("Should throw CategoryInUseException when category contains products")
    void givenCategoryContainingProductsWhenDeleteCategoryThenThrowCategoryInUseException() {

        Product product = new Product();

        product.setId(UUID.randomUUID());

        category.setProducts(List.of(product));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        assertThatThrownBy(() ->
                categoryService.deleteCategory(categoryId))

                .isInstanceOf(CategoryInUseException.class)

                .hasMessage("Cannot delete category because it contains active products.");

        verify(categoryRepository)
                .findById(categoryId);

        verify(categoryRepository, never())
                .delete(any());

    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when deleting non existing category")
    void givenInvalidCategoryIdWhenDeleteCategoryThenThrowCategoryNotFoundException() {

        UUID invalidCategoryId = UUID.randomUUID();

        when(categoryRepository.findById(invalidCategoryId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.deleteCategory(invalidCategoryId))

                .isInstanceOf(CategoryNotFoundException.class)

                .hasMessage("Category not found");

        verify(categoryRepository)
                .findById(invalidCategoryId);

        verify(categoryRepository, never())
                .delete(any());

    }
}
