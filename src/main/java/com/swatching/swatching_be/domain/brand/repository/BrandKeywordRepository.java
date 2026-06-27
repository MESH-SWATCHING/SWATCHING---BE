package com.swatching.swatching_be.domain.brand.repository;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandKeywordRepository extends JpaRepository<BrandKeyword, Long> {

    List<BrandKeyword> findByBrandIn(List<Brand> brands);
}
