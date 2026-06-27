package com.swatching.swatching_be.domain.archive.repository;

import com.swatching.swatching_be.domain.archive.entity.SavedBrand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedBrandRepository extends JpaRepository<SavedBrand, Long> {

    Long countByUserId(Long userId);
}
