package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Transaction;
import org.intern.personalfinancemanagementsystem.domain.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findTransactionByUserId(UUID id, Pageable pageable);

    List<Transaction> findTransactionByUserId(UUID id);

    Optional<Transaction> findByIdAndUserId(UUID transactionId, UUID userId);

    @Query("""
                SELECT COALESCE(SUM(t.amount),0) FROM Transaction t
                WHERE t.category.id = :categoryId
                    AND t.type = :type
                    AND t.transactionDate >= :startDate
                    AND t.transactionDate < :endDate
            """)
    BigDecimal calculateSpent(
            @Param("categoryId") UUID categoryId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("""
                    SELECT COALESCE(SUM(t.amount), 0)
                    FROM Transaction t
                    WHERE t.wallet.id = :walletId
                    AND t.type = :type
                    AND t.transactionDate >= :startDate
                    AND t.transactionDate >= :endDate
            """)
    BigDecimal sumAmountByWalletAndType(
            @Param("walletId") UUID walletId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    long countByWalletIdAndTransactionDateBetween(UUID walletId, LocalDate startDate, LocalDate endDate);
}