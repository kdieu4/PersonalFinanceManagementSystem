package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.WalletRepository;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletServiceImpl implements WalletService {
    WalletRepository walletRepository;
    UserService userService;

    @Override
    public PageResponse<List<WalletDetailResponse>> getAllWallets(UUID userId, int pageNo, int pageSize) {
        Page<Wallet> page = walletRepository.findWalletByUserId(userId, PageRequest.of(pageNo, pageSize));

        List<WalletDetailResponse> list = page.stream().map(WalletDetailResponse::from).toList();

        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    @Override
    public UUID addWallet(UUID userId, WalletRequest request) {
        User user = userService.getReferenceById(userId);
        if (walletRepository.existsByNameAndUserId(request.name(), userId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Wallet.WALLET_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
        Wallet wallet = Wallet.builder()
                .user(user)
                .name(request.name())
                .balance(request.balance())
                .currency(request.currency())
                .build();
        walletRepository.save(wallet);
        log.info("Wallet has added successfully, wallet_id={}", wallet.getId());
        return wallet.getId();
    }
}
