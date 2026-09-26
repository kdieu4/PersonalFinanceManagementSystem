package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Page<Wallet> findWalletByUserId(UUID userId, Pageable pageable);

    boolean existsByNameAndUserId(String name, UUID userId);

    boolean existsByNameAndUserIdAndIdNot(String name, UUID userId, UUID walletId);

    Optional<Wallet> findByIdAndUserId(UUID walletId, UUID userId);

    @Modifying
    @Query("UPDATE Wallet w SET w.archivedAt = :deleteTime WHERE w.id = :walletId AND w.user.id = :userId")
    void softDeleteWallet(@Param("walletId") UUID walletId, @Param("deleteTime") Instant deleteTime, @Param("userId") UUID userId);
}
