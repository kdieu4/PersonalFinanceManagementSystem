package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Page<Wallet> findWalletByUserId(UUID userId, Pageable pageable);

    boolean existsByNameAndUserId(String name, UUID userId);

    boolean existsByNameAndUserIdAndIdNot(String name, UUID userId, UUID walletId);

    Optional<Wallet> findByIdAndUserId(UUID walletId, UUID userId);
}
