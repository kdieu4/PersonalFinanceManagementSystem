package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    Page<Goal> findGoalByUserId(UUID userId, Pageable pageable);

    Optional<Goal> findByIdAndUserId(UUID goalId, UUID userId);

    boolean existsByNameAndUserIdAndIdNot(String name, UUID userId, UUID goalId);

    @Modifying
    @Query("UPDATE Goal b SET b.archivedAt = :deleteTime WHERE b.id = :goalId AND b.user.id = :userId")
    int softDeleteGoal(@Param("goalId") UUID walletId, @Param("deleteTime") Instant deleteTime, @Param("userId") UUID userId);
}