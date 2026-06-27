package com.swatching.swatching_be.domain.archive.repository;

import com.swatching.swatching_be.domain.archive.entity.mapping.SavedBrandCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedBrandCategoryRepository extends JpaRepository<SavedBrandCategory, Long> {
}
