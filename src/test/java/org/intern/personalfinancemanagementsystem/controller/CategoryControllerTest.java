package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private CustomUserDetails principal;

    @Mock
    private CategoryRequest request;

    private CategoryController categoryController;

    private UUID userId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryController = new CategoryController(categoryService);

        userId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        when(principal.getId()).thenReturn(userId);
    }

    @Test
    void getAllCategoriesByUser_whenValidRequest_shouldReturnOk() {
        int pageNo = 0;
        int pageSize = 20;

        PageResponse<List<CategoryDetailResponse>> serviceResponse = mock(PageResponse.class);

        when(categoryService.getAllCategoriesByUser(userId, pageNo, pageSize)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<PageResponse<List<CategoryDetailResponse>>>> response = categoryController.getAllCategoriesByUser(principal, pageNo, pageSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(categoryService).getAllCategoriesByUser(userId, pageNo, pageSize);
    }

    @Test
    void getCategoryDetail_whenCategoryExists_shouldReturnOk() {
        CategoryDetailResponse serviceResponse =
                mock(CategoryDetailResponse.class);

        when(categoryService.getCategoryDetail(userId, categoryId)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<CategoryDetailResponse>> response = categoryController.getAllCategoriesByUser(principal, categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(categoryService).getCategoryDetail(userId, categoryId);
    }

    @Test
    void addCategoryByUser_whenValidRequest_shouldReturnOk() {
        UUID createdCategoryId = UUID.randomUUID();
        when(categoryService.addCategory(userId, request)).thenReturn(createdCategoryId);

        ResponseEntity<ApiResponse<UUID>> response = categoryController.addCategoryByUser(principal, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(categoryService).addCategory(userId, request);
    }

    @Test
    void updateCategoryByUser_whenValidRequest_shouldReturnOk() {
        ResponseEntity<ApiResponse<UUID>> response = categoryController.updateCategoryByUser(principal, categoryId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(categoryService).updateCategory(userId, categoryId, request);
    }

    @Test
    void deleteCategoryByUser_whenCategoryExists_shouldReturnOk() {
        ResponseEntity<ApiResponse<Void>> response = categoryController.deleteCategoryByUser(principal, categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(categoryService).deleteCategory(userId, categoryId);
    }
}