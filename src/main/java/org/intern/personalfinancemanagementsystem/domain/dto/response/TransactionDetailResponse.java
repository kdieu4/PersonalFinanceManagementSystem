package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Transaction;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionDetailResponse(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        String purpose,
        String description,
        LocalDate transactionDate,
        UUID walletId,
        String walletName,
        UUID categoryId,
        String categoryName
) {
    public static TransactionDetailResponse from(Transaction transaction) {
        return new TransactionDetailResponse(transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getPurpose(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getWallet().getId(),
                transaction.getWallet().getName(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName());
    }
}
