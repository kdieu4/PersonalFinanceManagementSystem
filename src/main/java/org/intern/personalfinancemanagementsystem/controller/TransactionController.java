package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.TransactionDetailResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestApiV1
@Validated
@Slf4j
@Tag(name = "transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {
    TransactionService transactionService;

    @Operation(description = "Them vi cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(UrlConstant.Transaction.PREFIX)
    public ResponseEntity<ApiResponse<UUID>> addTransaction(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody TransactionRequest request
    ) {
        UUID response = transactionService.addTransaction(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Transaction.ADD_TRANSACTION_SUCCESSFULLY, response));
    }

    @Operation(summary = "Lay danh sach giao dich cua nguoi dung hien tai")
    @GetMapping(UrlConstant.Transaction.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResponse<List<TransactionDetailResponse>>>> getAllCategoriesByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        PageResponse<List<TransactionDetailResponse>> response = transactionService.getAllTransaction(principal.getId(), pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Transaction.GET_ALL_TRANSACTIONS_SUCCESSFULLY, response));
    }

    @Operation(summary = "Lay thong tin mot giao dich cua nguoi dung hien tai")
    @GetMapping(UrlConstant.Transaction.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> getAllCategoriesByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID transactionId) {
        TransactionDetailResponse response = transactionService.getTransactionDetail(principal.getId(), transactionId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Transaction.GET_DETAIL_TRANSACTION_SUCCESSFULLY, response));
    }

    @Operation(summary = "Xuat file csv nguoi dung hien tai")
    @GetMapping(UrlConstant.Transaction.EXPORT)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<byte[]> exportTransaction(@AuthenticationPrincipal CustomUserDetails principal) {
        byte[] response = transactionService.exportTransaction(principal.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(response);
    }
}
