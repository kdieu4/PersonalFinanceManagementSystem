package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
