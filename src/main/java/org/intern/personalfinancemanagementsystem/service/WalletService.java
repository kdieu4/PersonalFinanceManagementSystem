package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {
    PageResponse<List<WalletDetailResponse>> getAllWallets(UUID userId, int pageNo, int pageSize);

    UUID addWallet(UUID userId, WalletRequest request);

    void updateWallet(UUID userId, UUID walletID, WalletRequest request);

    void deleteWallet(UUID userId, UUID walletID);

    WalletDetailResponse getWalletDetail(UUID userId, UUID walletId);

    Wallet getReferenceById(UUID walletId);

    void updateBalance(UUID walletId, BigDecimal amount, TransactionType type);
}
