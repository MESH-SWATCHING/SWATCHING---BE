package com.swatching.swatching_be.domain.archive.repository;

import com.swatching.swatching_be.domain.archive.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

    List<UserCategory> findAllByUserId(Long userId);

    @Query("SELECT uc.id, COUNT(sbc) FROM UserCategory uc " +
            "LEFT JOIN SavedBrandCategory sbc ON sbc.userCategory = uc " +
            "WHERE uc.userId = :userId GROUP BY uc.id")
    List<Object[]> countBrandsByCategory(@Param("userId") Long userId);
}
