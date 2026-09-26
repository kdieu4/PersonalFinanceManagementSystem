package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.BudgetRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.BudgetResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Budget;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.BudgetRepository;
import org.intern.personalfinancemanagementsystem.repository.TransactionRepository;
import org.intern.personalfinancemanagementsystem.service.BudgetService;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService {
    BudgetRepository budgetRepository;
    UserService userService;
    TransactionRepository transactionRepository;
    CategoryService categoryService;

    @Override
    @Transactional
    public UUID addBudget(UUID userId, BudgetRequest request) {
        User user = userService.getReferenceById(userId);
        Category category = categoryService.findByIdAndUserId(request.categoryId(), userId);
        Budget budget = Budget.builder()
                .name(request.name())
                .user(user)
                .category(category)
                .amount(request.amount())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
        budget = budgetRepository.save(budget);
        return budget.getId();
    }

    @Override
    public PageResponse<List<BudgetResponse>> getAllBudget(UUID userId, int pageNo, int pageSize) {
        Page<Budget> page = budgetRepository.findBudgetByUserId(userId, PageRequest.of(pageNo, pageSize));
        List<BudgetResponse> list = page.stream().map(this::buildBudgetResponse).toList();
        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    private BudgetResponse buildBudgetResponse(Budget budget) {
        BigDecimal spent = transactionRepository.calculateSpent(
                budget.getCategory().getId(),
                TransactionType.EXPENSE,
                budget.getStartDate(),
                budget.getEndDate().plusDays(1));

        BigDecimal remaining = budget.getAmount().subtract(spent);

        return BudgetResponse.from(budget, spent, remaining);
    }

    @Override
    public BudgetResponse getBudgetById(UUID userId, UUID budgetId) {
        Budget budget = findByIdAndUserId(budgetId, userId);
        return buildBudgetResponse(budget);
    }

    @Override
    @Transactional
    public void updateBudgetById(UUID userId, UUID budgetId, BudgetRequest request) {
        Budget budget = findByIdAndUserId(budgetId, userId);
        if (budgetRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, budgetId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Budget.BUDGET_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
        Category category = categoryService.findByIdAndUserId(request.categoryId(), userId);
        budget.setName(request.name());
        budget.setCategory(category);
        budget.setAmount(request.amount());
        budget.setStartDate(request.startDate());
        budget.setEndDate(request.endDate());
        log.info("Budget has updated successfully, budget_id={}", budget.getId());
    }

    @Override
    @Transactional
    public void softDeleteBudgetById(UUID userId, UUID budgetId) {
        int updated = budgetRepository.softDeleteBudget(budgetId, Instant.now(), userId);
        log.info("Budget has deleted successfully, budget_id={}, updated_row={}", budgetId, updated);
        if (updated == 0) {
            throw new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Budget.BUDGET_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE
            );
        }
    }

    private Budget findByIdAndUserId(UUID budgetId, UUID userId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Budget.BUDGET_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }
}
