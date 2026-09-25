package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.BudgetRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.BudgetResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestApiV1
@Validated
@Tag(name = "budgets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BudgetController {
    BudgetService budgetService;

    @Operation(summary = "Them ngan sach")
    @PostMapping(UrlConstant.Budget.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UUID>> addBudget(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody BudgetRequest request
    ) {
        UUID response = budgetService.addBudget(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessMessage.Budget.ADD_BUDGET_SUCCESSFULLY, response));
    }

    @Operation(summary = "Lay danh sach ngan sach")
    @GetMapping(UrlConstant.Budget.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResponse<List<BudgetResponse>>>> getAllBudget(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize
    ) {
        PageResponse<List<BudgetResponse>> response = budgetService.getAllBudget(principal.getId(), pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Budget.GET_ALL_BUDGETS_SUCCESSFULLY, response));
    }

    @Operation(summary = "Xem chi tiet ngan sach")
    @GetMapping(UrlConstant.Budget.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID budgetId
    ) {
        BudgetResponse response = budgetService.getBudgetById(principal.getId(), budgetId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Budget.GET_DETAIL_BUDGET_SUCCESSFULLY, response));
    }

    @Operation(summary = "Sua ngan sach")
    @PutMapping(UrlConstant.Budget.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> updateBudgetById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID budgetId,
            @Valid @RequestBody BudgetRequest request
    ) {
        budgetService.updateBudgetById(principal.getId(), budgetId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Budget.UPDATE_BUDGET_SUCCESSFULLY, null));
    }

    @Operation(summary = "Xoa ngan sach")
    @DeleteMapping(UrlConstant.Budget.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteBudgetById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID budgetId
    ) {
        budgetService.softDeleteBudgetById(principal.getId(), budgetId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Budget.DELETE_BUDGET_SUCCESSFULLY, null));
    }
}