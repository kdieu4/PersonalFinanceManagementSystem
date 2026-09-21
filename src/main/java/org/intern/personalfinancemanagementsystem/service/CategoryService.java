package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    PageResponse<List<CategoryDetailResponse>> getAllCategoriesByUser(String email, int pageNo, int pageSize);

    UUID addCategory(String email, CategoryRequest request);

    void updateCategory(String email, UUID categoryId, CategoryRequest request);

    void deleteCategory(UUID id);
}