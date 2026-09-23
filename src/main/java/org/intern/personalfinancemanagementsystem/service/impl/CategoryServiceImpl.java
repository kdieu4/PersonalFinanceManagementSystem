package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.CategoryRepository;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    UserService userService;
    CategoryRepository categoryRepository;

    @Override
    public PageResponse<List<CategoryDetailResponse>> getAllCategoriesByUser(UUID userId, int pageNo, int pageSize) {
        Page<Category> page = categoryRepository.findCategoryByUserId(userId, PageRequest.of(pageNo, pageSize));

        List<CategoryDetailResponse> list = page.stream().map(CategoryDetailResponse::from).toList();

        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    @Override
    @Transactional
    public UUID addCategory(UUID userId, CategoryRequest request) {
        User user = userService.getReferenceById(userId);
        Category parent = findByIdAndUserId(request.parentId(), userId);

        checkCategoryExisted(request.name(), userId);

        Category category = Category.builder()
                .user(user)
                .parent(parent)
                .name(request.name())
                .type(request.type())
                .build();

        category = categoryRepository.save(category);

        String path = parent != null ? parent.getPath() + "/" + category.getId() : category.getId().toString();

        category.setPath(path);

        log.info("Category has added successfully, category_id={}", category.getId());
        return category.getId();
    }

    @Override
    @Transactional
    public void updateCategory(UUID userId, UUID categoryId, CategoryRequest request) {
        Category category = findByIdAndUserId(categoryId, userId);

        if (categoryId.equals(request.parentId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Category.ERR_CYCLIC_CATEGORY, ErrorMessage.BAD_REQUEST_CODE);
        }
        Category parent = findByIdAndUserId(request.parentId(), userId);

        checkCategoryExistedForUpdate(request.name(), userId, categoryId);

        category.setParent(parent);
        category.setName(request.name());
        category.setType(request.type());

        log.info("Category has updated successfully, category_id={}", category.getId());
    }

    @Override
    @Transactional
    public void deleteCategory(UUID userId, UUID categoryId) {
        String searchPath = categoryId.toString() + "%";
        categoryRepository.softDeleteCategory(searchPath, Instant.now(), userId);
    }

    @Override
    public CategoryDetailResponse getCategoryDetail(UUID userId, UUID id) {
        Category category = findByIdAndUserId(id, userId);
        List<Category> children = categoryRepository.findByParentId(category.getId());
        List<CategoryDetailResponse> res = children.stream().map(CategoryDetailResponse::from).toList();
        return CategoryDetailResponse.from(category, res);
    }

    @Override
    public Category getReferenceById(UUID categoryId) {
        return categoryRepository.getReferenceById(categoryId);
    }

    private Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Category.CATEGORY_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }

    private Category findByIdAndUserId(UUID id, UUID userId) {
        if (id == null) return null;
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Category.CATEGORY_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }

    private void checkCategoryExistedForUpdate(String name, UUID userId, UUID categoryId) {
        if (categoryRepository.existsByNameAndUserIdAndIdNot(name, userId, categoryId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Category.CATEGORY_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
    }

    private void checkCategoryExisted(String name, UUID userId) {
        if (categoryRepository.existsByNameAndUserId(name, userId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Category.CATEGORY_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
    }
}