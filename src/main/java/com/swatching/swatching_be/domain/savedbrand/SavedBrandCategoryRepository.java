package com.swatching.swatching_be.domain.savedbrand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavedBrandCategoryRepository extends JpaRepository<SavedBrandCategory, Long> {

    void deleteAllByCategoryId(Long categoryId);

    @Query("SELECT sbc.category.id, COUNT(sbc) FROM SavedBrandCategory sbc " +
            "WHERE sbc.savedBrand.user.id = :userId GROUP BY sbc.category.id")
    List<Object[]> countBrandsByCategoryRaw(@Param("userId") Long userId);
}
