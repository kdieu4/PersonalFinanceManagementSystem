package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestApiV1
@Validated
@Tag(name = "transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {
    TransactionService transactionService;

    @Operation(description = "Them vi cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(UrlConstant.Transaction.PREFIX)
    public ResponseEntity<ApiResponse<UUID>> addTransaction(
            @Valid @RequestBody TransactionRequest request
    ) {
        UUID response = transactionService.addTransaction(request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Transaction.ADD_TRANSACTION_SUCCESSFULLY, response));
    }
}
