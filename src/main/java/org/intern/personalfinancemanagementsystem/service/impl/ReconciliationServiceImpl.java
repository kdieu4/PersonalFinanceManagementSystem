package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.response.ReconciliationResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.TransactionRepository;
import org.intern.personalfinancemanagementsystem.repository.WalletRepository;
import org.intern.personalfinancemanagementsystem.service.ReconciliationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReconciliationServiceImpl implements ReconciliationService {
    WalletRepository walletRepository;
    TransactionRepository transactionRepository;

    @Override
    public ReconciliationResponse getReconciliation(UUID userId, UUID walletId, LocalDate startDate, LocalDate endDate) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Wallet.WALLET_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
        BigDecimal totalIncome = transactionRepository.sumAmountByWalletAndType(walletId, TransactionType.INCOME, startDate, endDate);

        BigDecimal totalExpense = transactionRepository.sumAmountByWalletAndType(walletId, TransactionType.EXPENSE, startDate, endDate);

        BigDecimal netChange = totalIncome.subtract(totalExpense);
        BigDecimal actualBalance = wallet.getBalance();
        BigDecimal openingBalance = actualBalance.subtract(netChange);

        BigDecimal expectedBalance = openingBalance.add(totalIncome).subtract(totalExpense);
        BigDecimal difference = actualBalance.subtract(expectedBalance);

        long transactionCount = transactionRepository.countByWalletIdAndTransactionDateBetween(walletId, startDate, endDate);

        return new ReconciliationResponse(
                wallet.getId(),
                wallet.getName(),
                startDate, endDate,
                openingBalance, totalIncome,
                totalExpense, expectedBalance,
                actualBalance, difference, transactionCount
        );
    }
}