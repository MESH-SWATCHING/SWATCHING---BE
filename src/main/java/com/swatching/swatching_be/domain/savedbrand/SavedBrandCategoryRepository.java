package com.swatching.swatching_be.domain.savedbrand;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedBrandCategoryRepository extends JpaRepository<SavedBrandCategory, Long> {

    void deleteAllByCategoryId(Long categoryId);
}
