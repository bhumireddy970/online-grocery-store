package com.grocery.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.product.dto.CategoryRequest;
import com.grocery.product.dto.CategoryResponse;
import com.grocery.product.exception.CategoryNotFoundException;
import com.grocery.product.exception.DuplicateCategoryException;
import com.grocery.product.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private UUID categoryId;

    private CategoryRequest categoryRequest;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {

        categoryId = UUID.randomUUID();

        categoryRequest =
                new CategoryRequest(

                        "Fruits",

                        "Fresh Fruits"

                );

        categoryResponse =
                new CategoryResponse(

                        categoryId,

                        "Fruits",

                        "Fresh Fruits",

                        LocalDateTime.now(),

                        LocalDateTime.now()

                );

    }

    @Test
    @DisplayName("Should create category when request is valid")
    void givenValidCategoryRequestWhenCreateCategoryThenReturnCreatedCategory() throws Exception {

        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        mockMvc.perform(

                        post("/api/categories")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(categoryRequest))

                )

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id")
                        .value(categoryId.toString()))

                .andExpect(jsonPath("$.name")
                        .value("Fruits"))

                .andExpect(jsonPath("$.description")
                        .value("Fresh Fruits"));

        verify(categoryService)
                .createCategory(any(CategoryRequest.class));

    }

    @Test
    @DisplayName("Should return bad request when category request is invalid")
    void givenInvalidCategoryRequestWhenCreateCategoryThenReturnBadRequest() throws Exception {

        CategoryRequest request =
                new CategoryRequest(

                        "",

                        ""

                );

        mockMvc.perform(

                        post("/api/categories")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return conflict when category already exists")
    void givenDuplicateCategoryWhenCreateCategoryThenReturnConflict() throws Exception {

        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenThrow(

                        new DuplicateCategoryException(

                                "Category already exists"

                        )

                );

        mockMvc.perform(

                        post("/api/categories")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(categoryRequest))

                )

                .andExpect(status().isConflict());

    }

    @Test
    @DisplayName("Should return validation error when category name is blank")
    void givenBlankCategoryNameWhenCreateCategoryThenReturnBadRequest() throws Exception {

        CategoryRequest request =
                new CategoryRequest(

                        "",

                        "Fresh Fruits"

                );

        mockMvc.perform(

                        post("/api/categories")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return all categories when categories exist")
    void givenExistingCategoriesWhenGetAllCategoriesThenReturnCategoryList() throws Exception {

        CategoryResponse secondCategory =
                new CategoryResponse(

                        UUID.randomUUID(),

                        "Vegetables",

                        "Fresh Vegetables",

                        LocalDateTime.now(),

                        LocalDateTime.now()

                );

        when(categoryService.getAllCategories())
                .thenReturn(

                        List.of(

                                categoryResponse,

                                secondCategory

                        )

                );

        mockMvc.perform(

                        get("/api/categories")

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(2))

                .andExpect(jsonPath("$[0].name")
                        .value("Fruits"))

                .andExpect(jsonPath("$[1].name")
                        .value("Vegetables"));

        verify(categoryService)
                .getAllCategories();

    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void givenNoCategoriesWhenGetAllCategoriesThenReturnEmptyList() throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(List.of());

        mockMvc.perform(

                        get("/api/categories")

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(0));

        verify(categoryService)
                .getAllCategories();

    }

    @Test
    @DisplayName("Should return category when category exists")
    void givenExistingCategoryIdWhenGetCategoryThenReturnCategoryResponse() throws Exception {

        when(categoryService.getCategoryById(categoryId))
                .thenReturn(categoryResponse);

        mockMvc.perform(

                        get("/api/categories/{id}", categoryId)

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(categoryId.toString()))

                .andExpect(jsonPath("$.name")
                        .value("Fruits"))

                .andExpect(jsonPath("$.description")
                        .value("Fresh Fruits"));

        verify(categoryService)
                .getCategoryById(categoryId);

    }

    @Test
    @DisplayName("Should return not found when category does not exist")
    void givenInvalidCategoryIdWhenGetCategoryThenReturnNotFound() throws Exception {

        UUID invalidCategoryId = UUID.randomUUID();

        when(categoryService.getCategoryById(invalidCategoryId))
                .thenThrow(

                        new CategoryNotFoundException(

                                "Category not found"

                        )

                );

        mockMvc.perform(

                        get("/api/categories/{id}", invalidCategoryId)

                )

                .andExpect(status().isNotFound());

        verify(categoryService)
                .getCategoryById(invalidCategoryId);

    }

    @Test
    @DisplayName("Should update category when request is valid")
    void givenValidCategoryRequestWhenUpdateCategoryThenReturnUpdatedCategory() throws Exception {

        CategoryRequest updateRequest =
                new CategoryRequest(

                        "Organic Fruits",

                        "Organic Seasonal Fruits"

                );

        CategoryResponse response =
                new CategoryResponse(

                        categoryId,

                        "Organic Fruits",

                        "Organic Seasonal Fruits",

                        LocalDateTime.now(),

                        LocalDateTime.now()

                );

        when(categoryService.updateCategory(categoryId, updateRequest))
                .thenReturn(response);

        mockMvc.perform(

                        put("/api/categories/{id}", categoryId)

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(updateRequest))

                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.name")
                        .value("Organic Fruits"))

                .andExpect(jsonPath("$.description")
                        .value("Organic Seasonal Fruits"));

        verify(categoryService)
                .updateCategory(categoryId, updateRequest);

    }

    @Test
    @DisplayName("Should return bad request when update request is invalid")
    void givenInvalidCategoryRequestWhenUpdateCategoryThenReturnBadRequest() throws Exception {

        CategoryRequest request =
                new CategoryRequest(

                        "",

                        ""

                );

        mockMvc.perform(

                        put("/api/categories/{id}", categoryId)

                                .contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))

                )

                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should delete category when category exists")
    void givenExistingCategoryIdWhenDeleteCategoryThenReturnSuccessMessage() throws Exception {

        mockMvc.perform(

                        delete("/api/categories/{id}", categoryId)

                )

                .andExpect(status().isOk())

                .andExpect(content().string("Category deleted successfully."));

        verify(categoryService)
                .deleteCategory(categoryId);

    }

    @Test
    @DisplayName("Should return not found when deleting non existing category")
    void givenInvalidCategoryIdWhenDeleteCategoryThenReturnNotFound() throws Exception {

        doThrow(

                new CategoryNotFoundException(

                        "Category not found"

                )

        )

                .when(categoryService)

                .deleteCategory(categoryId);

        mockMvc.perform(

                        delete("/api/categories/{id}", categoryId)

                )

                .andExpect(status().isNotFound());

    }

}
