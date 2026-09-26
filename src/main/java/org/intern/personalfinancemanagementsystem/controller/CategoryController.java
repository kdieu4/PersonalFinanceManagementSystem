package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestApiV1
@Validated
@Tag(name = "categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @Operation(summary = "Lay danh sach danh muc cua nguoi dung hien tai")
    @GetMapping(UrlConstant.Category.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResponse<List<CategoryDetailResponse>>>> getAllCategoriesByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        PageResponse<List<CategoryDetailResponse>> response = categoryService.getAllCategoriesByUser(principal.getId(), pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Category.GET_ALL_CATEGORIES_SUCCESSFULLY, response));
    }

    @Operation(summary = "Lay thong tin mot danh muc cua nguoi dung hien tai")
    @GetMapping(UrlConstant.Category.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> getAllCategoriesByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID categoryId) {
        CategoryDetailResponse response = categoryService.getCategoryDetail(principal.getId(), categoryId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Category.GET_DETAIL_CATEGORY_SUCCESSFULLY, response));
    }

    @Operation(summary = "Them danh muc cua nguoi dung hien tai")
    @PostMapping(UrlConstant.Category.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UUID>> addCategoryByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid CategoryRequest request) {
        UUID response = categoryService.addCategory(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Category.ADD_CATEGORY_SUCCESSFULLY, response));
    }

    @Operation(summary = "Cap nhat danh muc cua nguoi dung hien tai")
    @PutMapping(UrlConstant.Category.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UUID>> updateCategoryByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID categoryId,
            @Valid CategoryRequest request) {
        categoryService.updateCategory(principal.getId(), categoryId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Category.UPDATE_CATEGORY_SUCCESSFULLY, null));
    }

    @Operation(summary = "Xoa danh muc cua nguoi dung hien tai")
    @PatchMapping(UrlConstant.Category.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID categoryId
    ) {
        categoryService.deleteCategory(principal.getId(), categoryId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Category.DELETE_CATEGORY_SUCCESSFULLY, null));
    }
}
