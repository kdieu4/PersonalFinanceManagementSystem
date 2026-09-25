package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findTransactionByUserId(UUID id, Pageable pageable);

    List<Transaction> findTransactionByUserId(UUID id);

    Optional<Transaction> findByIdAndUserId(UUID transactionId, UUID userId);
}
