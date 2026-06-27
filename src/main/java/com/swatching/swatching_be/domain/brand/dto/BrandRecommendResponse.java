package com.swatching.swatching_be.domain.brand.dto;

import java.util.List;

public record BrandRecommendResponse(
        Long brandId,
        String name,
        String summary,
        String mainImageUrl,
        List<String> keywords
) {
}