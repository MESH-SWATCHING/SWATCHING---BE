package com.swatching.swatching_be.brand.dto;

import java.util.List;

public record BrandRecommendResponse(
        Long brandId,
        String name,
        String oneLineIntro,
        String thumbnailUrl,
        List<String> keywords
) {
}