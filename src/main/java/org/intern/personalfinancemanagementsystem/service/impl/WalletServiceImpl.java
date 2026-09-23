package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
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
    @Transactional
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
        wallet = walletRepository.save(wallet);
        log.info("Wallet has added successfully, wallet_id={}", wallet.getId());
        return wallet.getId();
    }

    @Override
    @Transactional
    public void updateWallet(UUID userId, UUID walletId, WalletRequest request) {
        if (walletRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, walletId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Wallet.WALLET_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
        Wallet wallet = findWalletByIdAndUserId(walletId, userId);

        wallet.setName(request.name());
        wallet.setBalance(request.balance());
        wallet.setCurrency(request.currency());
        log.info("Wallet has updated successfully, wallet_id={}", wallet.getId());
    }

    @Override
    @Transactional
    public void deleteWallet(UUID userId, UUID walletID) {
        walletRepository.softDeleteWallet(walletID, Instant.now(), userId);
        log.info("Wallet has deleted successfully, wallet_id={}", walletID);
    }

    @Override
    public WalletDetailResponse getWalletDetail(UUID userId, UUID walletId) {
        Wallet wallet = findWalletByIdAndUserId(walletId, userId);
        return WalletDetailResponse.from(wallet);
    }

    @Override
    public Wallet getReferenceById(UUID walletId) {
        return walletRepository.getReferenceById(walletId);
    }

    @Override
    public void updateBalance(UUID walletId, BigDecimal amount, TransactionType type) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.WALLET_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
        BigDecimal currentBalance = wallet.getBalance();
        BigDecimal newBalance;
        if (type.equals(TransactionType.EXPENSE)) {
            if (amount.compareTo(currentBalance) > 0) {
                throw new AppException(HttpStatus.BAD_REQUEST, ErrorMessage.Transaction.AMOUNT_INVALID, ErrorMessage.BAD_REQUEST_CODE);
            }
            newBalance = currentBalance.subtract(amount);
        } else {
            newBalance = currentBalance.add(amount);
        }
        wallet.setBalance(newBalance);
    }

    private Wallet findWalletByIdAndUserId(UUID walletId, UUID userId) {
        return walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.WALLET_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }
}
