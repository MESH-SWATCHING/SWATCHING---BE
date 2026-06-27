package com.swatching.swatching_be.brand.dto;

import java.util.List;

public record BrandDetailResponse(
        Long brandId,
        String name,
        String oneLineIntro,
        String story,
        String thumbnailUrl,
        String instagramUrl,
        String websiteUrl,
        List<String> keywords,
        List<String> visuals
) {
}