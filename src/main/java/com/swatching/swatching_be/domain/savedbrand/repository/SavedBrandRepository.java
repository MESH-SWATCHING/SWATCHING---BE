package com.swatching.swatching_be.domain.savedbrand.repository;

import com.swatching.swatching_be.domain.savedbrand.SavedBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedBrandRepository extends JpaRepository<SavedBrand, Long> {

    Optional<SavedBrand> findByIdAndUserId(Long id, Long userId);

    Long countByUserId(Long userId);

    boolean existsByUserIdAndBrandId(Long userId, Long brandId);
}
