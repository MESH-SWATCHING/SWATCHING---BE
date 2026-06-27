package com.swatching.swatching_be.domain.brand.repository;

import com.swatching.swatching_be.domain.brand.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("SELECT DISTINCT b FROM Brand b JOIN BrandKeyword bk ON bk.brand = b WHERE bk.keyword.name IN :keywords")
    List<Brand> findBrandsHavingAnyKeyword(@Param("keywords") List<String> keywords);
}
