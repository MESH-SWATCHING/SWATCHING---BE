package com.swatching.swatching_be.domain.manualbrand.dto;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandSourceType;
import java.util.List;

public record ManualBrandResponse(
        Long savedBrandId,
        Long brandId,
        String name,
        BrandSourceType sourceType,
        boolean isManual,
        List<Long> categoryIds
) {

    public static ManualBrandResponse of(Long savedBrandId, Brand brand, List<Long> categoryIds) {
        return new ManualBrandResponse(
                savedBrandId,
                brand.getId(),
                brand.getName(),
                brand.getSourceType(),
                true,
                categoryIds
        );
    }
}
