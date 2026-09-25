package org.intern.personalfinancemanagementsystem.repository;

import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.domain.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    @Modifying
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.category.id = :categoryId
            AND t.type = 'EXPENSE'
            AND t.transactionDate >= :startDate
            AND t.transactionDate < :endDate
            """)
    BigDecimal calculateSpent(UUID categoryId, LocalDate startDate, LocalDate endDate);

    Page<Budget> findBudgetByUserId(UUID userId, Pageable pageable);

    Optional<Budget> findByIdAndUserId(UUID budgetId, UUID userId);

    boolean existsByNameAndUserIdAndIdNot(String name, UUID userId, UUID budgetId);

    @Modifying
    @Query("UPDATE Budget b SET b.archivedAt = :deleteTime WHERE b.id = :budgetId AND b.user.id = :userId")
    int softDeleteBudget(@Param("budgetId") UUID walletId, @Param("deleteTime") Instant deleteTime, @Param("userId") UUID userId);
}