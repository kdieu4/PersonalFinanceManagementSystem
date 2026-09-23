package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    PageResponse<List<WalletDetailResponse>> getAllWallets(UUID userId, int pageNo, int pageSize);

    UUID addWallet(UUID userId, WalletRequest request);

    void updateWallet(UUID userId, UUID walletID, WalletRequest request);
}
