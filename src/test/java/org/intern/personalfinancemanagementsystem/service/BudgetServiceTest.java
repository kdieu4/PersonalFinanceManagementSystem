package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.BudgetRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.BudgetResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.*;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.BudgetRepository;
import org.intern.personalfinancemanagementsystem.repository.TransactionRepository;
import org.intern.personalfinancemanagementsystem.service.impl.BudgetServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.CategoryServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {
    @Mock
    UserServiceImpl userService;

    @Mock
    CategoryServiceImpl categoryService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    BudgetRepository budgetRepository;

    @InjectMocks
    BudgetServiceImpl budgetService;

    private UUID userId;
    private UUID budgetId;
    private UUID categoryId;

    private User user;
    private Category category;
    private Budget budget;
    private BudgetRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        budgetId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        user = User.builder().id(userId).build();
        category = Category.builder().id(categoryId).name("Thuc pham").build();
        budget = Budget.builder()
                .id(budgetId)
                .category(category)
                .name("Ngan sach an uong")
                .amount(new BigDecimal("5000000"))
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 30))
                .build();

        request = new BudgetRequest("Ngan sach an uong", categoryId, new BigDecimal("5000000"), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));
    }

    @Test
    void addBudget_whenValidRequest_shouldReturnBudgetId() {
        when(userService.getReferenceById(userId)).thenReturn(user);
        when(categoryService.findByIdAndUserId(categoryId, userId)).thenReturn(category);

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> {
                    Budget savedBudget = invocation.getArgument(0);
                    savedBudget.setId(budgetId);
                    return savedBudget;
                });

        UUID result = budgetService.addBudget(userId, request);

        assertNotNull(result);
    }

    @Test
    void getAllBudget_whenValidRequest_shouldReturnPageBudgets() {
        int pageNo = 0;
        int pageSize = 10;

        Page<Budget> page = new PageImpl<>(List.of(budget), PageRequest.of(pageNo, pageSize), 1);

        when(budgetRepository.findBudgetByUserId(eq(userId), eq(PageRequest.of(pageNo, pageSize)))).thenReturn(page);
        when(transactionRepository.calculateSpent(
                eq(categoryId),
                eq(TransactionType.EXPENSE),
                eq(LocalDate.of(2026, 9, 1)),
                eq(LocalDate.of(2026, 10, 1))
        )).thenReturn(new BigDecimal("2000000"));

        var result = budgetService.getAllBudget(userId, pageNo, pageSize);

        assertNotNull(result);
    }

    @Test
    void getGoal_whenValidId_shouldReturnBudget() {
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));

        when(transactionRepository.calculateSpent(
                eq(categoryId), eq(TransactionType.EXPENSE),
                eq(LocalDate.of(2026, 9, 1)),
                eq(LocalDate.of(2026, 10, 1))
        )).thenReturn(new BigDecimal("2000000"));

        BudgetResponse result = budgetService.getBudgetById(userId, budgetId);

        assertNotNull(result);
    }

    @Test
    void getGoal_whenInValidId_shouldThrowNotFound() {
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> budgetService.getBudgetById(userId, budgetId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateBudgetById_whenValidRequest_shouldUpdateSuccessfully() {
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, budgetId)).thenReturn(false);

        when(categoryService.findByIdAndUserId(categoryId, userId)).thenReturn(category);

        budgetService.updateBudgetById(userId, budgetId, request);

        assertEquals(request.name(), budget.getName());
    }

    @Test
    void updateBudgetById_whenNameAlreadyExists_whenThrowConflict() {
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, budgetId)).thenReturn(true);
        AppException exception = assertThrows(AppException.class, () -> {
            budgetService.updateBudgetById(userId, budgetId, request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(categoryService, never()).findByIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void softDeleteBudgetById_whenBudgetExists_shouldDeleteSuccess() {
        when(budgetRepository.softDeleteBudget(eq(budgetId), any(Instant.class), eq(userId))).thenReturn(1);
        assertDoesNotThrow(() -> budgetService.softDeleteBudgetById(userId, budgetId));
        verify(budgetRepository).softDeleteBudget(eq(budgetId), any(Instant.class), eq(userId));
    }

    @Test
    void softDeleteBudgetById_whenBudgetDoesNotExist_shouldThrowNotFound() {
        when(budgetRepository.softDeleteBudget(eq(budgetId), any(Instant.class), eq(userId))).thenReturn(0);
        AppException exception = assertThrows(AppException.class, () -> budgetService.softDeleteBudgetById(userId, budgetId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}