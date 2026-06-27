package com.swatching.swatching_be.domain.category.repository;

import com.swatching.swatching_be.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(Long userId);

    boolean existsByUserIdAndName(Long userId, String name);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Optional<Category> findByUserIdAndName(Long userId, String name);

    List<Category> findAllByIdInAndUserId(Collection<Long> ids, Long userId);
}
