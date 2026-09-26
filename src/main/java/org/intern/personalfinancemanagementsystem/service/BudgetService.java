package org.intern.personalfinancemanagementsystem.service;


import jakarta.validation.Valid;
import org.intern.personalfinancemanagementsystem.domain.dto.request.BudgetRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.BudgetResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface BudgetService {
    UUID addBudget(UUID userId, BudgetRequest request);

    PageResponse<List<BudgetResponse>> getAllBudget(UUID userId, int pageNo, int pageSize);

    BudgetResponse getBudgetById(UUID id, UUID budgetId);

    void updateBudgetById(UUID id, UUID budgetId, @Valid BudgetRequest request);

    void softDeleteBudgetById(UUID id, UUID budgetId);
}
