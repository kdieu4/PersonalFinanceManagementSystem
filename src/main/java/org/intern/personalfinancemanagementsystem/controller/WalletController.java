package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestApiV1
@Validated
@Tag(name = "wallets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletController {
    WalletService walletService;

    @Operation(description = "Lay danh sach vi cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(UrlConstant.Wallet.PREFIX)
    public ResponseEntity<ApiResponse<PageResponse<List<WalletDetailResponse>>>> getAllWallet(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize
    ) {
        PageResponse<List<WalletDetailResponse>> response = walletService.getAllWallets(principal.getId(), pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Wallet.GET_ALL_WALLETS_SUCCESSFULLY, response));
    }
}
