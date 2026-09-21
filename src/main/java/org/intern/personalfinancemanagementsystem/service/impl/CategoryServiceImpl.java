package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLRestriction;
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
//@SQLRestriction("archived_at IS NULL")
public class CategoryServiceImpl implements CategoryService {
    UserService userService;
    CategoryRepository categoryRepository;

    @Override
    public PageResponse<List<CategoryDetailResponse>> getAllCategoriesByUser(String email, int pageNo, int pageSize) {
        Page<Category> page = categoryRepository.findCategoryByUserEmail(email, PageRequest.of(pageNo, pageSize));

        List<CategoryDetailResponse> list = page.stream().map(CategoryDetailResponse::from).toList();

        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    @Override
    @Transactional
    public UUID addCategory(String email, CategoryRequest request) {
        User user = userService.findByEmail(email);
        Category parent = findByIdAndUserEmail(request.parentId(), email);

        checkCategoryExisted(request.name(), email);

        Category category = Category.builder()
                .user(user)
                .parent(parent)
                .name(request.name())
                .type(request.type())
                .build();

        categoryRepository.save(category);
        
        String path = parent != null ? parent.getPath() + "/" + category.getId() : category.getId().toString();

        category.setPath(path);

        log.info("Category has added successfully, category_id={}", category.getId());
        return category.getId();
    }

    @Override
    @Transactional
    public void updateCategory(String email, UUID categoryId, CategoryRequest request) {
        User user = userService.findByEmail(email);
        Category category = getCategoryById(categoryId);

        if (categoryId.equals(request.parentId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Category.ERR_CYCLIC_CATEGORY, ErrorMessage.BAD_REQUEST_CODE);
        }
        Category parent = findByIdAndUserEmail(request.parentId(), email);

        checkCategoryExistedForUpdate(request.name(), email, categoryId);

        category.setUser(user);
        category.setParent(parent);
        category.setName(request.name());
        category.setType(request.type());

        log.info("Category has updated successfully, category_id={}", category.getId());
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategoryById(id);
        String searchPath = id.toString() + "%";
        categoryRepository.softDeleteCategory(searchPath, Instant.now());
    }

    private Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Category.CATEGORY_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }

    private Category findByIdAndUserEmail(UUID id, String email) {
        if (id == null) return null;
        return categoryRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Category.CATEGORY_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }

    private void checkCategoryExistedForUpdate(String name, String email, UUID categoryId) {
        if (categoryRepository.existsByNameAndUserEmailAndIdNot(name, email, categoryId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Category.CATEGORY_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
    }

    private void checkCategoryExisted(String name, String email) {
        if (categoryRepository.existsByNameAndUserEmail(name, email)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Category.CATEGORY_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
    }
}