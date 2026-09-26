package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findCategoryByUserId(UUID id, Pageable pageable);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByNameAndUserId(String name, UUID userId);

    boolean existsByNameAndUserIdAndIdNot(String name, UUID userId, UUID id);

    @Modifying
    @Query("UPDATE Category c SET c.archivedAt = :deleteTime WHERE c.path LIKE :searchPath AND c.user.id = :userId")
    void softDeleteCategory(@Param("searchPath") String searchPath, @Param("deleteTime") Instant deleteTime, @Param("userId") UUID userId);

    List<Category> findByParentId(UUID id);
}