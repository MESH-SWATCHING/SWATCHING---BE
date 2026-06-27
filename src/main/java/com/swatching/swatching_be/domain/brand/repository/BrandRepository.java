package com.swatching.swatching_be.domain.brand.repository;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findAllByVisibility(BrandVisibility visibility);

    List<Brand> findAllByVisibilityOrderByCreatedAtDesc(BrandVisibility visibility);

    List<Brand> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT b FROM Brand b JOIN BrandKeyword bk ON bk.brand = b WHERE bk.keyword.name IN :keywords AND b.visibility = 'PUBLIC'")
    List<Brand> findBrandsHavingAnyKeyword(@Param("keywords") List<String> keywords);
}
