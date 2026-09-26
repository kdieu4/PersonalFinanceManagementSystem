package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.response.ReconciliationResponse;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.ReconciliationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@RestApiV1
@Validated
@Tag(name = "reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReconciliationController {
    ReconciliationService reconciliationService;

    @Operation(summary = "Bao cao doi soat giao dich")
    @GetMapping(UrlConstant.REPORTS)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReconciliationResponse>> getReconciliation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam UUID walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        if (endDate.isBefore(startDate)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Report.NOT_VALID_DATE, ErrorMessage.BAD_REQUEST_CODE);
        }

        ReconciliationResponse response = reconciliationService.getReconciliation(principal.getId(), walletId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Report.GET_REPORT_SUCCESS, response));
    }
}
