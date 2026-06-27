package com.swatching.swatching_be.domain.brand.repository;

import com.swatching.swatching_be.domain.brand.entity.BrandImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandImageRepository extends JpaRepository<BrandImage, Long> {

    List<BrandImage> findByBrand_IdOrderBySortOrderAsc(Long brandId);
}