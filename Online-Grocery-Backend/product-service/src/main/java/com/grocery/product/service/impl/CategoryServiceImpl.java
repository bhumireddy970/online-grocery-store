package com.grocery.product.service.impl;

import com.grocery.product.dto.CategoryRequest;
import com.grocery.product.dto.CategoryResponse;
import com.grocery.product.entity.Category;
import com.grocery.product.exception.CategoryInUseException;
import com.grocery.product.exception.CategoryNotFoundException;
import com.grocery.product.exception.DuplicateCategoryException;
import com.grocery.product.repository.CategoryRepository;
import com.grocery.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateCategoryException("Category already exists");
        }

        Category category = new Category();

        category.setName(request.name());
        category.setDescription(request.description());

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        return mapToResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(UUID id,
                                           CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        category.setName(request.name());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        if (!category.getProducts().isEmpty()) {
            throw new CategoryInUseException(
                    "Cannot delete category because it contains active products.");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(

                category.getId(),

                category.getName(),

                category.getDescription(),

                category.getCreatedAt(),

                category.getUpdatedAt()

        );
    }
}
