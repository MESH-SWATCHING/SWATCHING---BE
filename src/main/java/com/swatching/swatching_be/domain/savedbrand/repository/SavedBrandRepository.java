package com.swatching.swatching_be.domain.savedbrand.repository;

import com.swatching.swatching_be.domain.savedbrand.SavedBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SavedBrandRepository extends JpaRepository<SavedBrand, Long> {

    Optional<SavedBrand> findByIdAndUserId(Long id, Long userId);

    Long countByUserId(Long userId);

    boolean existsByUserIdAndBrandId(Long userId, Long brandId);

    List<SavedBrand> findAllByIdInAndUserId(Collection<Long> ids, Long userId);

    @Query("SELECT sb.brand.id FROM SavedBrand sb WHERE sb.user.id = :userId")
    Set<Long> findSavedBrandIdsByUserId(@Param("userId") Long userId);
}
