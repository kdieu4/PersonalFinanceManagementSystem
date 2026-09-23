package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.Transaction;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.repository.TransactionRepository;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.intern.personalfinancemanagementsystem.service.TransactionService;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    WalletService walletService;
    CategoryService categoryService;
    TransactionRepository transactionRepository;

    @Override
    @Transactional
    public UUID addTransaction(TransactionRequest request) {
        // 1. Lay reference cua wallet va category
        Wallet wallet = walletService.getReferenceById(request.walletId());
        Category category = categoryService.getReferenceById(request.categoryId());
        // 2. Cap nhat so du
        walletService.updateBalance(request.walletId(), request.amount(), request.type());
        // 3. Luu giao dich
        Transaction transaction = Transaction.builder()
                .purpose(request.purpose())
                .wallet(wallet)
                .category(category)
                .amount(request.amount())
                .type(request.type())
                .build();
        transaction = transactionRepository.save(transaction);
        return transaction.getId();
    }
}
