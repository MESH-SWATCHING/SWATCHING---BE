package com.swatching.swatching_be.domain.savedbrand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedBrandRepository extends JpaRepository<SavedBrand, Long> {

    Optional<SavedBrand> findByIdAndUserId(Long id, Long userId);
}
