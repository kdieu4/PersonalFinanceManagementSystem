package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @Operation(description = "Them vi cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(UrlConstant.Wallet.PREFIX)
    public ResponseEntity<ApiResponse<UUID>> addWallet(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody WalletRequest request
    ) {
        UUID response = walletService.addWallet(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Wallet.ADD_WALLET_SUCCESSFULLY, response));
    }

    @Operation(description = "Thay doi vi cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(UrlConstant.Wallet.BY_ID)
    public ResponseEntity<ApiResponse<UUID>> updateWallet(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID walletId,
            @Valid @RequestBody WalletRequest request
    ) {
        walletService.updateWallet(principal.getId(), walletId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Wallet.UPDATE_WALLET_SUCCESSFULLY, null));
    }

    @Operation(description = "Xoa vi cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(UrlConstant.Wallet.BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteWallet(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID walletId
    ) {
        walletService.deleteWallet(principal.getId(), walletId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Wallet.DELETE_WALLET_SUCCESSFULLY, null));
    }

    @Operation(description = "Xem chi tiet cua nguoi dung hien tai")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(UrlConstant.Wallet.BY_ID)
    public ResponseEntity<ApiResponse<WalletDetailResponse>> getWalletDetail(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID walletId
    ) {
        WalletDetailResponse response = walletService.getWalletDetail(principal.getId(), walletId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Wallet.DELETE_WALLET_SUCCESSFULLY, response));
    }
}
