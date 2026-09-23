package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    PageResponse<List<CategoryDetailResponse>> getAllCategoriesByUser(UUID userId, int pageNo, int pageSize);

    UUID addCategory(UUID userId, CategoryRequest request);

    void updateCategory(UUID userId, UUID categoryId, CategoryRequest request);

    void deleteCategory(UUID userId, UUID id);

    CategoryDetailResponse getCategoryDetail(UUID userId, UUID id);

    Category getReferenceById(UUID categoryId);
}