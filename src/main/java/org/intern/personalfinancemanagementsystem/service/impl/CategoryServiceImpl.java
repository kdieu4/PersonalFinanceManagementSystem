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
import org.intern.personalfinancemanagementsystem.repository.UserRepository;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    UserRepository userRepository;

    @Override
    public PageResponse<List<CategoryDetailResponse>> getAllCategoriesByUser(String email, int pageNo, int pageSize) {
        Page<Category> page = categoryRepository.findCategoryByUserEmail(email, PageRequest.of(pageNo, pageSize));

        List<CategoryDetailResponse> list = page.stream().map(CategoryDetailResponse::from).toList();

        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    @Override
    public UUID addCategory(String email, CategoryRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.User.USER_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));

        Category parent;
        if (!StringUtils.hasText(request.parentName())) {
            parent = null;
        } else {
            parent = categoryRepository.findByNameAndUserEmail(request.parentName(), email)
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Category.CATEGORY_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
        }

        if (categoryRepository.existsByNameAndUserEmail(request.name(), email)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Category.CATEGORY_EXISTED, ErrorMessage.CONFLICT_CODE);
        }

        Category category = Category.builder()
                .user(user)
                .parent(parent)
                .name(request.name())
                .type(request.type())
                .build();

        categoryRepository.save(category);
        log.info("Category has added successfully, category_id={}", category.getId());
        return category.getId();
    }
}