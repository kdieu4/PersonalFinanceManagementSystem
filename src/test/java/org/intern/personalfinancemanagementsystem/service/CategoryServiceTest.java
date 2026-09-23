package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.CategoryRepository;
import org.intern.personalfinancemanagementsystem.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    UserService userService;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    Page<Category> getAllCategoriesByUser;
    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    void getAllCategories_WhenValidRequest_ShouldReturnSuccess() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        int testPageNo = 0;
        int testPageSize = 20;

        when(categoryRepository.findCategoryByUserId(mockUser.getId(), PageRequest.of(testPageNo, testPageSize))).thenReturn(getAllCategoriesByUser);
        // 2. Act
        PageResponse<List<CategoryDetailResponse>> response = categoryService.getAllCategoriesByUser(mockUser.getId(), testPageNo, testPageSize);
        // 3. Assert
        Assertions.assertNotNull(response);
    }

    @Test
    void addCategory_WhenValidRequest_ShouldReturnSuccess() {
        // 1. Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(id);

        String name = "Sinh hoạt";
        String type = "Chi phí";
        id = UUID.randomUUID();
        Category mockCategory = new Category();
        mockCategory.setId(id);

        CategoryRequest request = new CategoryRequest(null, name, type);

        when(userService.getReferenceById(mockUser.getId())).thenReturn(mockUser);
        when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(mockCategory);
        // 2. Act
        UUID response = categoryService.addCategory(mockUser.getId(), request);
        // 3. Assert
        Assertions.assertNotNull(response);
    }

    @Test
    void addCategory_WhenCategoryExisted_ShouldThrowException() {
        // 1. Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(id);

        String name = "Sinh hoạt";
        String type = "Chi phí";
        id = UUID.randomUUID();
        Category mockCategory = new Category();
        mockCategory.setId(id);
        mockCategory.setName(name);

        CategoryRequest request = new CategoryRequest(null, name, type);

        when(userService.getReferenceById(mockUser.getId())).thenReturn(mockUser);
        when(categoryRepository.existsByNameAndUserId(mockCategory.getName(), mockUser.getId())).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            categoryService.addCategory(mockUser.getId(), request);
        });

        Assertions.assertEquals(ErrorMessage.Category.CATEGORY_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void addCategory_WhenParentIdNotFound_ShouldThrowException() {
        // 1. Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(id);

        String name = "Sinh hoạt";
        String type = "Chi phí";
        id = UUID.randomUUID();
        Category mockCategory = new Category();
        mockCategory.setId(id);
        mockCategory.setName(name);
        UUID testParentIdFound = UUID.randomUUID();
        Category parentCategory = new Category();
        UUID realParentId = UUID.randomUUID();
        parentCategory.setId(realParentId);
        mockCategory.setParent(parentCategory);

        CategoryRequest request = new CategoryRequest(testParentIdFound, name, type);

        when(userService.getReferenceById(mockUser.getId())).thenReturn(mockUser);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            categoryService.addCategory(mockUser.getId(), request);
        });

        Assertions.assertEquals(ErrorMessage.Category.CATEGORY_NOT_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void addCategory_WhenParentIdExists_ShouldReturnTrue() {
        // 1. Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(id);

        String name = "Sinh hoạt";
        String type = "Chi phí";

        id = UUID.randomUUID();
        Category mockCategory = new Category();
        mockCategory.setId(id);
        mockCategory.setName(name);
        UUID testParentIdFound = UUID.randomUUID();
        Category parentCategory = new Category();
        parentCategory.setId(testParentIdFound);
        mockCategory.setParent(parentCategory);

        CategoryRequest request = new CategoryRequest(testParentIdFound, name, type);

        when(userService.getReferenceById(mockUser.getId())).thenReturn(mockUser);
        when(categoryRepository.findByIdAndUserId(testParentIdFound, mockUser.getId())).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.existsByNameAndUserId(mockCategory.getName(), mockUser.getId())).thenReturn(false);
        when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(mockCategory);
        // 2. Act
        UUID response = categoryService.addCategory(mockUser.getId(), request);
        // 3. Assert
        Assertions.assertNotNull(response);

    }

    @Test
    void updateCategory_WhenValidRequest_ShouldReturnTrue() {
        UUID testUserId = UUID.randomUUID();
        UUID testCategoryId = UUID.randomUUID();
        String name = "Sinh hoạt";
        String type = "Chi phí";

        Category mockCategory = new Category();
        mockCategory.setId(testCategoryId);
        mockCategory.setName(name);
        mockCategory.setType(type);

        String updateName = "Bột giặt";
        String updateType = "Chi phí";

        CategoryRequest request = new CategoryRequest(null, updateName, updateType);

        when(categoryRepository.findByIdAndUserId(testCategoryId, testUserId)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.existsByNameAndUserIdAndIdNot(request.name(), testUserId, testCategoryId)).thenReturn(false);

        categoryService.updateCategory(testUserId, testCategoryId, request);

        Assertions.assertEquals(updateName, mockCategory.getName());
        Assertions.assertEquals(updateType, mockCategory.getType());
    }

    @Test
    void updateCategory_WhenCategoryExisted_ShouldThrowException() {
        UUID testUserId = UUID.randomUUID();
        UUID testCategoryId = UUID.randomUUID();
        String name = "Sinh hoạt";
        String type = "Chi phí";

        Category mockCategory = new Category();
        mockCategory.setId(testCategoryId);
        mockCategory.setName(name);
        mockCategory.setType(type);

        String updateName = "Bột giặt";
        String updateType = "Chi phí";

        CategoryRequest request = new CategoryRequest(null, updateName, updateType);

        when(categoryRepository.findByIdAndUserId(testCategoryId, testUserId)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.existsByNameAndUserIdAndIdNot(request.name(), testUserId, testCategoryId)).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            categoryService.updateCategory(testUserId, testCategoryId, request);
        });

        Assertions.assertEquals(ErrorMessage.Category.CATEGORY_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void updateCategory_WhenCyclicParent_ShouldThrowException() {
        UUID testUserId = UUID.randomUUID();
        UUID testCategoryId = UUID.randomUUID();
        String name = "Sinh hoạt";
        String type = "Chi phí";

        Category mockCategory = new Category();
        mockCategory.setId(testCategoryId);
        mockCategory.setName(name);
        mockCategory.setType(type);

        String updateName = "Bột giặt";
        String updateType = "Chi phí";

        CategoryRequest request = new CategoryRequest(testCategoryId, updateName, updateType);

        when(categoryRepository.findByIdAndUserId(testCategoryId, testUserId)).thenReturn(Optional.of(mockCategory));

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            categoryService.updateCategory(testUserId, testCategoryId, request);
        });

        Assertions.assertEquals(ErrorMessage.Category.ERR_CYCLIC_CATEGORY, caughtException.getErrorMessage());
    }

    @Test
    void deleteCategory_WhenValidRequest_ShouldReturnTrue() {
        UUID testUserId = UUID.randomUUID();
        UUID testCategoryId = UUID.randomUUID();
        categoryService.deleteCategory(testUserId, testCategoryId);
        Mockito.verify(categoryRepository, Mockito.times(1)).softDeleteCategory(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Instant.class), ArgumentMatchers.any(UUID.class));
    }

    @Test
    void getCategoryDetail_WhenValidRequest_ShouldReturnTrue() {
        UUID testUserId = UUID.randomUUID();
        UUID testCategoryId = UUID.randomUUID();
        Category mockCategory = new Category();
        mockCategory.setId(testCategoryId);
        when(categoryRepository.findByIdAndUserId(testCategoryId, testUserId)).thenReturn(Optional.of(mockCategory));
        CategoryDetailResponse response = categoryService.getCategoryDetail(testUserId, testCategoryId);
        Assertions.assertNotNull(response);
    }
}
