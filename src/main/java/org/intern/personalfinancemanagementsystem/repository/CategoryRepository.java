package org.intern.personalfinancemanagementsystem.repository;

import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findCategoryByUserEmail(String email, Pageable pageable);

    Optional<Category> findByNameAndUserEmail(String name, String email);

    boolean existsByNameAndUserEmail(String name, String email);
}