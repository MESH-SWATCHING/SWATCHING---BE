package com.swatching.swatching_be.brand.repository;

import com.swatching.swatching_be.brand.entity.BrandKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandKeywordRepository extends JpaRepository<BrandKeyword, Long> {

    List<BrandKeyword> findByBrand_BrandId(Long brandId);
}