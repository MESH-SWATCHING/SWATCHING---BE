package com.swatching.swatching_be.domain.brand.dto;

import java.util.List;

public record BrandDetailResponse(
        Long brandId,
        String name,
        String summary,
        String story,
        String storySummary,
        String mainImageUrl,
        String instagramUrl,
        String websiteUrl,
        List<String> keywords,
        List<String> visuals
) {
}