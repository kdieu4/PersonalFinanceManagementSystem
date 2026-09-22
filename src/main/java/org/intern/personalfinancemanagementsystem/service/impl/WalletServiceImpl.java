package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.repository.WalletRepository;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletServiceImpl implements WalletService {
    WalletRepository walletRepository;

    @Override
    public PageResponse<List<WalletDetailResponse>> getAllWallets(UUID userId, int pageNo, int pageSize) {
        Page<Wallet> page = walletRepository.findWalletByUserId(userId, PageRequest.of(pageNo, pageSize));

        List<WalletDetailResponse> list = page.stream().map(WalletDetailResponse::from).toList();

        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }
}
