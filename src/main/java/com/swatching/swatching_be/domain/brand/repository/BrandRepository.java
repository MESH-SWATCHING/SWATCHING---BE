package com.swatching.swatching_be.domain.brand.repository;

import com.swatching.swatching_be.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}