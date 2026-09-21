package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findCategoryByUserEmail(String email, Pageable pageable);

    Optional<Category> findByIdAndUserEmail(UUID id, String email);

    boolean existsByNameAndUserEmail(String name, String email);

    boolean existsByNameAndUserEmailAndIdNot(String name, String email, UUID id);

    @Modifying
    @Query("UPDATE Category c SET c.archivedAt = :deleteTime WHERE c.path LIKE :searchPath")
    void softDeleteCategory(@Param("searchPath") String searchPath, @Param("deleteTime") Instant deleteTime);
}